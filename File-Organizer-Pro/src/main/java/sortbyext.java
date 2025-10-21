import com.google.gson.Gson;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class sortbyext {
    public sortbyext(String link) {
        File mainfolder = new File(link);
        File[] filelist = mainfolder.listFiles();
        if (filelist == null) return;

        UndoManager undoManager = new UndoManager(); // Initialize UndoManager
        List<MoveOperation> currentMoveSession = new ArrayList<>(); // Store moves of this operation

        for (File f : filelist) {
            String filename = f.getName();
            int Dotindex = filename.lastIndexOf(".");
            String extension = "";

            if (Dotindex != -1 && Dotindex != filename.length() - 1) {
                extension = filename.substring(Dotindex);
                File folder = new File(link + "\\" + extension);
                folder.mkdir();

                Path source = Paths.get(link + "\\" + filename);
                Path destination = Paths.get(link + "\\" + extension + "\\" + filename);

                try {
                    Files.move(source, destination);

                    // Record this move for undo
                    currentMoveSession.add(new MoveOperation(source, destination));

                } catch (Exception e) {
                    System.out.println("something went wrong");
                }
            }
        }

        // Save the session with all moves to JSON for undo
        if (!currentMoveSession.isEmpty()) {
            undoManager.addMoveSession(currentMoveSession);
        }
    }
}
