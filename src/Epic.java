import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }
}
