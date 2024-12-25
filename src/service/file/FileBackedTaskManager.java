package service.file;

import model.*;
import service.Managers;
import service.history.HistoryManager;
import service.memory.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    int idGen = 0;

    public FileBackedTaskManager(File file) {
        super(Managers.getDefaultHistory());
        this.file = file;
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile();
        return manager;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        saveInFile();
        return newTask;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTask = super.createSubTask(subTask);
        saveInFile();
        return newSubTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        saveInFile();
        return newEpic;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        saveInFile();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        saveInFile();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        saveInFile();
    }

    @Override
    public void updateTask(Task task, Task updatedTask) {
        super.updateTask(task, updatedTask);
        saveInFile();
    }

    @Override
    public void updateSubTask(SubTask subTask, SubTask updatedSubTask) {
        super.updateSubTask(subTask, updatedSubTask);
        saveInFile();
    }

    @Override
    public void updateEpic(Epic epic, Epic updatedEpic) {
        super.updateEpic(epic, updatedEpic);
        saveInFile();
    }

    private void loadFromFile() {
        String string;
        try (final FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
             final BufferedReader bufReader = new BufferedReader(reader)) {
            bufReader.readLine();
            while (bufReader.ready()) {
                string = bufReader.readLine();
                fromString(string);
            }
        } catch (IOException e) {
            throw new FileException("Не удалось прочитать из файла");
        }
    }

    private void fromString(String string) {
        final String[] stringArr = string.split(", ");
        int id = Integer.parseInt(stringArr[0]);
        if (idGen < id) {
            idGen = id;
        }
        String name = stringArr[2];
        Status status = null;
        if (stringArr[3].equals("NEW")) {
            status = Status.NEW;
        } else if (stringArr[3].equals("IN_PROGRESS")) {
            status = Status.IN_PROGRESS;
        } else if (stringArr[3].equals("DONE")) {
            status = Status.DONE;
        }
        String description = stringArr[4];
        Duration duration = Duration.parse(stringArr[5]);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(stringArr[6]);
        String startTimeStr = startTime.format(formatter);
        Task task = new Task(id, Type.TASK, name, status, description, (int) duration.toMinutes(), startTimeStr);
        Epic epic = new Epic(id, Type.EPIC, name, status, description, (int) duration.toMinutes(), startTimeStr);
        SubTask subTask = new SubTask(id, Type.EPIC, name, status, description,
                (int) duration.toMinutes(), startTimeStr, epic);
        switch (stringArr[1]) {
            case "TASK":
                tasks.put(id, task);
                break;
            case "EPIC":
                epics.put(id, epic);
                break;
            case "SUBTASK":
                allSubTasks.put(id, subTask);
                epic.addSubTask(subTask);
                break;
        }
        calculateEpic(epic);
    }

    private void saveInFile() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.append("id, type, name, status, description, duration, startTime, epic");
            writer.newLine();
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, SubTask> entry : allSubTasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileException("Не удалось записать в файл");
        }
    }

    private String toString(Task task) {
        return task.getId() + ", " + task.getType() + ", " + task.getName() + ", " + task.getStatus()
                + ", " + task.getDescription() + ", " + task.getDuration() + ", " + task.getStartTime()
                + ", " + task.getEpicId();
    }

}
