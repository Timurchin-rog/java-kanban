package service.file;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Type;
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
    private int idGen = 0;

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
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubTask = super.createSubtask(subtask);
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
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        saveInFile();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        saveInFile();
    }

    @Override
    public void updateTask(int id, Task updatedTask) {
        super.updateTask(id, updatedTask);
        saveInFile();
    }

    @Override
    public void updateSubtask(int id, Subtask updatedSubtask) {
        super.updateSubtask(id, updatedSubtask);
        saveInFile();
    }

    @Override
    public void updateEpic(int id, Epic updatedEpic) {
        super.updateEpic(id, updatedEpic);
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
        String status = stringArr[3];
        String description = stringArr[4];
        Duration duration = Duration.ofMinutes(Integer.parseInt(stringArr[5].substring(2, stringArr[5].length() - 1)));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        String startTimeStr = stringArr[6].replace("T", " ")
                .replace("-", ".");
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        Task task = new Task(id, Type.TASK, name, status, description, duration, startTime);
        Epic epic = new Epic(id, Type.EPIC, name, status, description, duration, startTime);
        Subtask subTask = new Subtask(id, Type.SUBTASK, name, status, description,
                duration, startTime, epic.getId());
        switch (stringArr[1]) {
            case "TASK":
                getAllTasks().put(id, task);
                break;
            case "EPIC":
                getAllEpics().put(id, epic);
                break;
            case "SUBTASK":
                getAllSubtasks().put(id, subTask);
                epic.addSubtask(subTask);
                break;
        }
        calculateEpic(epic);
    }

    private void saveInFile() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.append("id, type, name, status, description, duration, startTime, epic");
            writer.newLine();
            for (Map.Entry<Integer, Task> entry : getAllTasks().entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : getAllEpics().entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Subtask> entry : getAllSubtasks().entrySet()) {
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
