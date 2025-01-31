package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    public Subtask(int id, Type type, String name, String status, String description,
                   Duration duration, LocalDateTime startTime, Integer epicId) {
        super(id, type, name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(Type type, String name, String status, String description,
                   Duration duration, LocalDateTime startTime, Integer epicId) {
        super(type, name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }
}
