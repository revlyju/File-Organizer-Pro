import java.nio.file.Path;

public class MoveOperation {
    private String sourcePath;
    private String destinationPath;

    public MoveOperation(Path source, Path destination) {
        this.sourcePath = source.toString();
        this.destinationPath = destination.toString();
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }
}

