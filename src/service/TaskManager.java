package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    HashMap<Integer, Task> printAllTasks();

    HashMap<Integer, SubTask> printAllSubTasks();

    HashMap<Integer, Epic> printAllEpics();

    void removeAllTasks();

    void removeAllSubTasks(Epic epic);

    void removeAllEpics();

    Task createTask(Task task);

    SubTask createSubTask(SubTask subTask);

    Epic createEpic(Epic epic);

    ArrayList<SubTask> getSubTasksOfEpic(Epic epic);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    void updateTask(Task task, String name, Status status, String description);

    void updateSubTask(SubTask subTask, String name, Status status, String description);

    void updateEpic(Epic epic, String name, String description);

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    List<Task> getHistory();
}
