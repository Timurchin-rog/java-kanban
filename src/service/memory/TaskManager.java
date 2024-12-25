package service.memory;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

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

    void updateTask(Task task, Task updatedTask);

    void updateSubTask(SubTask subTask, SubTask updatedSubTask);

    void updateEpic(Epic epic, Epic updatedEpic);

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
