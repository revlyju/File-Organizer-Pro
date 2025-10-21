import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Undo {

    private UndoManager undoManager;

    public Undo() {
        undoManager = new UndoManager();
    }

    // Returns [status message, main folder name]
    public String[] undoLastSort() {
        List<MoveOperation> lastSession = undoManager.getLastMoveSession();

        if (lastSession == null || lastSession.isEmpty()) {
            return new String[]{"No operation to undo.", ""};
        }

        // Safely extract the main folder name from the parent directory of the first source path
        String mainFolder = "";
        if (!lastSession.isEmpty()) {
            try {
                Path firstSource = Paths.get(lastSession.get(0).getSourcePath());
                Path mainFolderPath = firstSource.getParent();
                if (mainFolderPath != null) {
                    mainFolder = mainFolderPath.getFileName().toString();
                } else {
                    // If parent is null, use full root (edge case)
                    mainFolder = firstSource.toString();
                }
            } catch (Exception e) {
                mainFolder = "";
            }
        }

        boolean allMovedBack = true;
        Set<String> destinationFolders = new HashSet<>();

        for (MoveOperation op : lastSession) {
            try {
                Path currentLocation = Paths.get(op.getDestinationPath());
                Path originalLocation = Paths.get(op.getSourcePath());
                Files.move(currentLocation, originalLocation);
                destinationFolders.add(currentLocation.getParent().toString());
            } catch (Exception e) {
                allMovedBack = false;
            }
        }

        for (String folderPath : destinationFolders) {
            File folder = new File(folderPath);
            if (folder.isDirectory() && folder.list().length == 0) {
                folder.delete();
            }
        }

        if (allMovedBack) {
            undoManager.removeLastMoveSession();
            return new String[]{"Undo completed! All files and folders were restored and cleaned up.", mainFolder};
        } else {
            return new String[]{"Undo finished with some errors. Some files or folders may not have been restored/deleted.", mainFolder};
        }
    }
}