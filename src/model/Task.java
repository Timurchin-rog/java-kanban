package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private final Type type;
    private String name;
    private Status status;
    private String description;
    Epic epic;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public Task(int id, Type type, String name, Status status, String description,
                int duration, String startTime) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(Type type, String name, Status status, String description,
                int duration, String startTime) {
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(Type type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.duration = Duration.ofMinutes(45);
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now();
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

    public void setName(String name) {
        this.name = name;
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

    public Epic getEpic() {
        return null;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
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
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
