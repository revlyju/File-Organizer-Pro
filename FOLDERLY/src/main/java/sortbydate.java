import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class sortbydate {
    public sortbydate(String link, String datetype) {
        File mainfolder = new File(link);
        File[] filelist = mainfolder.listFiles();
        if (filelist == null) return;

        String con = "";
        if (datetype.equals("date")) {
            con = "yyyy-MM-dd";
        } else if (datetype.equals("month")) {
            con = "yyyy-MM";
        } else if (datetype.equals("year")) {
            con = "yyyy";
        }

        UndoManager undoManager = new UndoManager();  // initialize UndoManager
        List<MoveOperation> currentMoveSession = new ArrayList<>(); // list of moves this session

        for (File f : filelist) {
            String filename = f.getName();
            long lastmodified = f.lastModified();
            Date date = new Date(lastmodified);
            SimpleDateFormat sd = new SimpleDateFormat(con);
            String fm = sd.format(date);

            File datefile = new File(link, fm);
            datefile.mkdir();

            Path source = Paths.get(link, filename);
            Path destination = Paths.get(link, fm, filename);

            try {
                Files.move(source, destination);

                // Record each move for undo
                currentMoveSession.add(new MoveOperation(source, destination));

            } catch (Exception e) {
                System.out.println("something went wrong moving " + filename);
                e.printStackTrace();
            }
        }

        // Save the whole session moves for undo
        if (!currentMoveSession.isEmpty()) {
            undoManager.addMoveSession(currentMoveSession);
        }
    }
}
