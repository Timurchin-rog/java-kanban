package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, SubTask> allSubTasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Task> tasks;
    protected final HistoryManager historyManager;
    int idGen = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.allSubTasks = new HashMap<>();
    }

    private int generateID() {
        return ++idGen;
    }

    @Override
    public HashMap<Integer, Task> printAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, SubTask> printAllSubTasks() {
        return allSubTasks;
    }

    @Override
    public HashMap<Integer, Epic> printAllEpics() {
        return epics;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks(Epic epic) {
        allSubTasks.clear();
        epic.removeAllSubTask();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateID());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpic().getId());
        epic.addSubTask(subTask);
        subTask.setId(generateID());
        allSubTasks.put(subTask.getId(), subTask);
        calculateStatus(epic);
        return subTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateID());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public ArrayList<SubTask> getSubTasksOfEpic(Epic epic) {
        return epic.subTasks;
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        if (allSubTasks.get(id) != null) {
            historyManager.add(allSubTasks.get(id));
        }
        return allSubTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public void updateTask(Task task, String name, Status status, String description) {
        task.setName(name);
        task.setStatus(status);
        task.setDescription(description);
    }

    @Override
    public void updateSubTask(SubTask subTask, String name, Status status, String description) {
        subTask.setName(name);
        subTask.setStatus(status);
        subTask.setDescription(description);
        Epic epic = subTask.getEpic();
        epic.removeSubTask(subTask);
        epic.addSubTask(subTask);
        calculateStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic, String name, String description) {
        epic.setName(name);
        epic.setDescription(description);
    }

    private void calculateStatus(Epic epic) {
        int i = 0;
        int j = 0;
        for (SubTask subTask : epic.subTasks) {
            if (subTask.getStatus().equals(Status.DONE)) {
                i++;
            }
            if (subTask.getStatus().equals(Status.NEW)) {
                j++;
            }
        }
        if (epic.subTasks.size() == i) {
            epic.setStatus(Status.DONE);
        }
        if (epic.subTasks.size() == j) {
            epic.setStatus(Status.NEW);
        }
        if (!(epic.subTasks.size() == i) && !(epic.subTasks.size() == j)) {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        historyManager.remove(id);
        SubTask subTask = allSubTasks.get(id);
        allSubTasks.remove(id);
        subTask.removeSubTask(subTask);
    }

    @Override
    public void removeEpic(int id) {
        historyManager.remove(id);
        Epic removedEpic = epics.get(id);
        epics.remove(id);
        removeSubTaskOfEpic(removedEpic);
        removedEpic.removeAllSubTask();
    }

    private void removeSubTaskOfEpic(Epic removedEpic) {
        for (int i = 0; i < removedEpic.subTasks.size(); i++) {
            if (allSubTasks.get(removedEpic.subTasks.get(i).getId()) != null) {
                allSubTasks.remove(removedEpic.subTasks.get(i).getId());
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}