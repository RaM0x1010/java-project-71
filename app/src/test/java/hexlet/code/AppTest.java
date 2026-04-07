package hexlet.code;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppTest {

    private App app;
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        app = new App();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGenerateDiffIdenticalFiles() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("host", "hexlet.io");
        data.put("timeout", 50);
        data.put("proxy", "123.234.53.22");
        data.put("follow", false);

        File file1 = createTempJsonFile("file1.json", data);
        File file2 = createTempJsonFile("file2.json", data);

        Map<String, Object> map1 = app.readAndParseFile(file1);
        Map<String, Object> map2 = app.readAndParseFile(file2);

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "    follow: false\n"
                + "    host: hexlet.io\n"
                + "    proxy: 123.234.53.22\n"
                + "    timeout: 50\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffAddedKeys() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("host", "hexlet.io");
        map1.put("timeout", 50);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("host", "hexlet.io");
        map2.put("timeout", 50);
        map2.put("verbose", true);

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "    host: hexlet.io\n"
                + "    timeout: 50\n"
                + "  + verbose: true\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffRemovedKeys() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("host", "hexlet.io");
        map1.put("timeout", 50);
        map1.put("proxy", "123.234.53.22");

        // Второй файл с двумя ключами
        Map<String, Object> map2 = new HashMap<>();
        map2.put("host", "hexlet.io");
        map2.put("timeout", 50);

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "    host: hexlet.io\n"
                + "  - proxy: 123.234.53.22\n"
                + "    timeout: 50\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffChangedValues() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("host", "hexlet.io");
        map1.put("timeout", 50);
        map1.put("follow", false);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("host", "hexlet.io");
        map2.put("timeout", 20);
        map2.put("follow", true);

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "  - follow: false\n"
                + "  + follow: true\n"
                + "    host: hexlet.io\n"
                + "  - timeout: 50\n"
                + "  + timeout: 20\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffMixedChanges() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("host", "hexlet.io");
        map1.put("timeout", 50);
        map1.put("proxy", "123.234.53.22");
        map1.put("follow", false);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("timeout", 20);
        map2.put("verbose", true);
        map2.put("host", "hexlet.io");

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "  - follow: false\n"
                + "    host: hexlet.io\n"
                + "  - proxy: 123.234.53.22\n"
                + "  - timeout: 50\n"
                + "  + timeout: 20\n"
                + "  + verbose: true\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffDifferentValueTypes() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("stringKey", "string value");
        map1.put("numberKey", 42);
        map1.put("booleanKey", false);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("stringKey", "different string");
        map2.put("numberKey", 100);
        map2.put("booleanKey", true);

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "  - booleanKey: false\n"
                + "  + booleanKey: true\n"
                + "  - numberKey: 42\n"
                + "  + numberKey: 100\n"
                + "  - stringKey: string value\n"
                + "  + stringKey: different string\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffEmptyFiles() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffFirstEmptySecondHasData() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key1", "value1");
        map2.put("key2", 123);

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "  + key1: value1\n"
                + "  + key2: 123\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffFirstHasDataSecondEmpty() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", 123);

        Map<String, Object> map2 = new HashMap<>();

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "  - key1: value1\n"
                + "  - key2: 123\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testGenerateDiffSortingOrder() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("zebra", "animal");
        map1.put("apple", "fruit");
        map1.put("car", "vehicle");

        Map<String, Object> map2 = new HashMap<>(map1);

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "    apple: fruit\n"
                + "    car: vehicle\n"
                + "    zebra: animal\n"
                + "}";

        assertEquals(expected, diff);
    }

    @Test
    void testReadAndParseFileValidJson() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "test");
        data.put("value", 123);
        data.put("active", true);

        File jsonFile = createTempJsonFile("test.json", data);

        Map<String, Object> parsed = app.readAndParseFile(jsonFile);

        assertEquals("test", parsed.get("name"));
        assertEquals(123, parsed.get("value"));
        assertEquals(true, parsed.get("active"));
        assertEquals(3, parsed.size());
    }

    @Test
    void testReadAndParseFileFileNotFound() {
        File nonExistentFile = new File(tempDir.toFile(), "nonexistent.json");

        assertThrows(IOException.class, () -> {
            app.readAndParseFile(nonExistentFile);
        });
    }

    @Test
    void testReadAndParseFileInvalidJson() throws IOException {
        Path invalidJsonFile = tempDir.resolve("invalid.json");
        Files.writeString(invalidJsonFile, "{invalid json content");

        assertThrows(Exception.class, () -> {
            app.readAndParseFile(invalidJsonFile.toFile());
        });
    }

    @Test
    void testGenerateDiffNullValues() throws IOException {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", null);
        map1.put("key2", "value");

        Map<String, Object> map2 = new HashMap<>();
        map2.put("key1", null);
        map2.put("key2", "different");

        String diff = app.generateDiff(map1, map2);
        String expected = "{\n"
                + "    key1: null\n"
                + "  - key2: value\n"
                + "  + key2: different\n"
                + "}";

        assertEquals(expected, diff);
    }


    private File createTempJsonFile(String fileName, Map<String, Object> data) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        objectMapper.writeValue(file, data);
        return file;
    }
}
