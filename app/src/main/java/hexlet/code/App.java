package hexlet.code;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
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
        System.out.println("Comparing files:");
        System.out.println("  File 1: " + filepath1.getAbsolutePath());
        System.out.println("  File 2: " + filepath2.getAbsolutePath());
        System.out.println("  Format: " + format);
        System.out.println("Running gendiff with format: " + format);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
