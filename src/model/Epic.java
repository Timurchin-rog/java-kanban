package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(int id, Type type, String name, String status, String description,
                Duration duration, LocalDateTime startTime) {
        super(id, type, name, status, description, duration, startTime);
    }

    public Epic(Type type, String name, String description) {
        super(type, name, description);
    }

    public void addSubtask(Subtask subTask) {
        subtasks.add(subTask);
    }

    public void removeSubtask(Subtask subTask) {
        subtasks.remove(subTask);
    }

    public void removeAllSubtask() {
        subtasks.clear();
    }
}
