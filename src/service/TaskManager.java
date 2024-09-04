package service;
import model.Epic;
import model.SubTask;
import model.Task;
import model.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, SubTask> allSubTasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Task> tasks;
    int idGen = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.allSubTasks = new HashMap<>();
    }

    private int generateID() {
        return ++idGen;
    }

    public Object printAllTasks() {
        return tasks;
    }

    public Object printAllSubTasks() {
        return allSubTasks;
    }

    public Object printAllEpics() {
        return epics;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubTasks(Epic epic) {
        allSubTasks.clear();
        epic.removeAllSubTask();
    }

    public void removeAllEpics() {
        epics.clear();
    }

    public Task createTask(Task task) {
        task.setId(generateID());
        tasks.put(task.getId(), task);
        return task;
    }

    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpic().getId());
        epic.addSubTask(subTask);
        subTask.setId(generateID());
        allSubTasks.put(subTask.getId(), subTask);
        return subTask;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateID());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public ArrayList<SubTask> getSubTasksOfEpic(Epic epic) {
        return epic.subTasks;
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTask(int id) {
        return allSubTasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task, String name, Status status, String description) {
        task.setName(name);
        task.setStatus(status);
        task.setDescription(description);
    }

    public void updateSubTask(Epic epic, SubTask subTask, String name, Status status, String description) {
        subTask.setName(name);
        subTask.setStatus(status);
        subTask.setDescription(description);
        epic.removeSubTask(subTask);
        epic.addSubTask(subTask);
        calculateStatus(epic);


    }

    public void updateEpic(Epic epic, String name, String description) {
        epic.setName(name);
        epic.setDescription(description);
    }

    public void calculateStatus(Epic epic) {
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


    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubTask(int id) {
        SubTask subTask = allSubTasks.get(id);
        System.out.println(subTask);
        allSubTasks.remove(id);
        subTask.removeSubTask(subTask);
    }

    public void removeEpic(int id) {
        Epic removedEpic = epics.get(id);
        epics.remove(id);
        removedEpic.removeAllSubTask();
    }


}
