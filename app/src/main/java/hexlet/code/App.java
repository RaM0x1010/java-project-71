package hexlet.code;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

@Command(
        name = "gendiff",
        description = "Compares two configuration files and shows a difference.",
        mixinStandardHelpOptions = true,
        version = "gendiff 1.0"
)

public class App implements Callable<Integer> {

    @Parameters(
            index = "0",
            description = "path to first file",
            paramLabel = "filepath1"
    )
    private File filepath1;

    @Parameters(
            index = "1",
            description = "path to second file",
            paramLabel = "filepath2"
    )
    private File filepath2;

    @Option(
            names = {"-f", "--format"},
            description = "output format [default: ${DEFAULT-VALUE}]",
            defaultValue = "stylish"
    )

    private String format;

    @Override
    public Integer call() throws Exception {
        Map<String, Object> data1 = readAndParseFile(filepath1);
        Map<String, Object> data2 = readAndParseFile(filepath2);
        String diff = generateDiff(data1, data2);
        System.out.println(diff);
        return 0;
    }

    public Map<String, Object> readAndParseFile(File file) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, Map.class);
    }

    public String generateDiff(Map<String, Object> map1, Map<String, Object> map2) {
        Set<String> allKeys = new TreeSet<>();
        allKeys.addAll(map1.keySet());
        allKeys.addAll(map2.keySet());

        StringBuilder result = new StringBuilder();
        result.append("{\n");

        for (String key : allKeys) {
            boolean inFirst = map1.containsKey(key);
            boolean inSecond = map2.containsKey(key);
            Object value1 = map1.get(key);
            Object value2 = map2.get(key);

            if (inFirst && inSecond) {
                if (Objects.equals(value1, value2)) {
                    result.append("    ").append(key).append(": ").append(formatValue(value1)).append("\n");
                } else {
                    result.append("  - ").append(key).append(": ").append(formatValue(value1)).append("\n");
                    result.append("  + ").append(key).append(": ").append(formatValue(value2)).append("\n");
                }
            } else if (inFirst && !inSecond) {
                result.append("  - ").append(key).append(": ").append(formatValue(value1)).append("\n");
            } else if (!inFirst && inSecond) {
                result.append("  + ").append(key).append(": ").append(formatValue(value2)).append("\n");
            }
        }

        result.append("}");
        return result.toString();
    }

    private String formatValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value == null) {
            return "null";
        }
        return value.toString();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
