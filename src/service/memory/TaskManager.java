package service.memory;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    HashMap<Integer, Task> getAllTasks();

    HashMap<Integer, Subtask> getAllSubtasks();

    HashMap<Integer, Epic> getAllEpics();

    void removeAllTasks();

    void removeAllEpics();

    Task createTask(Task task);

    Subtask createSubtask(Subtask subtask);

    Epic createEpic(Epic epic);

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    void updateTask(int id, Task updatedTask);

    void updateSubtask(int id, Subtask updatedSubtask);

    void updateEpic(int id, Epic updatedEpic);

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
