import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UndoManager {

    private static final String JSON_FILE = "undostuff.json";
    private static final int HISTORY_LIMIT = 10; // Keep last 5 undo sessions

    private Gson gson;
    private List<List<MoveOperation>> allMoveSessions;

    public UndoManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        allMoveSessions = loadFromFile();
        if (allMoveSessions == null) {
            allMoveSessions = new ArrayList<>();
        }
    }

    public void addMoveSession(List<MoveOperation> moveOperations) {
        if (moveOperations == null || moveOperations.isEmpty()) {
            return;
        }
        allMoveSessions.add(moveOperations);

        // Remove oldest session(s) if exceeding history limit
        while (allMoveSessions.size() > HISTORY_LIMIT) {
            allMoveSessions.remove(0);
        }

        saveToFile();
    }

    public List<MoveOperation> getLastMoveSession() {
        if (allMoveSessions == null || allMoveSessions.isEmpty()) {
            return null;
        }
        List<MoveOperation> last = allMoveSessions.get(allMoveSessions.size() - 1);
        return (last == null || last.isEmpty()) ? null : last;
    }

    public void removeLastMoveSession() {
        if (allMoveSessions != null && !allMoveSessions.isEmpty()) {
            allMoveSessions.remove(allMoveSessions.size() - 1);
            saveToFile();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(JSON_FILE)) {
            gson.toJson(allMoveSessions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<List<MoveOperation>> loadFromFile() {
        try (Reader reader = new FileReader(JSON_FILE)) {
            Type collectionType = new TypeToken<List<List<MoveOperation>>>() {}.getType();
            return gson.fromJson(reader, collectionType);
        } catch (FileNotFoundException e) {
            // File doesn't exist yet
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
