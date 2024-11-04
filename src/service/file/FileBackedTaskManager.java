package service.file;

import model.*;
import service.InMemoryTaskManager;
import service.Managers;
import service.history.HistoryManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
    public void updateTask(Task task, String name, Status status, String description) {
        super.updateTask(task, name, status, description);
        saveInFile();
    }

    @Override
    public void updateSubTask(SubTask subTask, String name, Status status, String description) {
        super.updateSubTask(subTask, name, status, description);
        saveInFile();
    }

    @Override
    public void updateEpic(Epic epic, String name, String description) {
        super.updateEpic(epic, name, description);
        saveInFile();
    }

    private void loadFromFile() {
        String string = "";
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
        Task task = new Task(id, Type.TASK, name, status, description);
        Epic epic = new Epic(id, Type.EPIC, name, status, description);
        SubTask subTask = new SubTask(id, Type.EPIC, name, status, description, epic);
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
    }

    private void saveInFile() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.append("id, type, name, status, description, epic");
            writer.newLine();
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toStringTask(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(toStringTask(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, SubTask> entry : allSubTasks.entrySet()) {
                writer.append(toStringSubTask(entry.getValue()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileException("Не удалось записать в файл");
        }
    }

    private String toStringTask(Task task) {
        return task.getId() + ", " + task.getType() + ", " + task.getName() + ", " + task.getStatus() + ", "
                + task.getDescription();
    }

    private String toStringSubTask(SubTask subTask) {
        return subTask.getId() + ", " + subTask.getType() + ", " + subTask.getName() + ", " + subTask.getStatus() + ", "
                + subTask.getDescription() + ", " + subTask.getEpic().getName();
    }

}
