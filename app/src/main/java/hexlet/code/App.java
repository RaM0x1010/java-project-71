package hexlet.code;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

@Command(
        name = "gendiff",
        description = "Compares two configuration files and shows a difference.",
        mixinStandardHelpOptions = true,
        version = "gendiff 1.0"
)

public class App implements Callable<Integer> {

    @Option(names = {"-f", "--format"},
            description = "Output format (default: ${DEFAULT-VALUE})")
    private String format = "stylish";

    @Override
    public Integer call() throws Exception {
        // Your business logic for comparing files would go here
        // For now, just print that the app is running
        System.out.println("Running gendiff with format: " + format);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
