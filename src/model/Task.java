package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private final Type type;
    private String name;
    private Status status;
    private String description;
    protected Integer epicId;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(int id, Type type, String name, String status, String description,
                Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = convertStringToStatus(status);
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(Type type, String name, String status, String description,
                Duration duration, LocalDateTime startTime) {
        this.type = type;
        this.name = name;
        this.status = convertStringToStatus(status);
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(Type type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.duration = Duration.ofMinutes(45);
        this.startTime = LocalDateTime.now();
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(int id, Type type, String name, String status, String description, Duration duration,
                LocalDateTime startTime, LocalDateTime endTime, Integer epicId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = convertStringToStatus(status);
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.epicId = epicId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEpicId() {
        return null;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Status convertStringToStatus(String statusStr) {
        return switch (statusStr) {
            case "DONE" -> Status.DONE;
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            default -> Status.NEW;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && status == task.status && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
