package service.memory;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public final HashMap<Integer, SubTask> allSubTasks;
    public final HashMap<Integer, Epic> epics;
    public final HashMap<Integer, Task> tasks;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
        if (!prioritizedTasks.isEmpty())
            checkTaskTime(task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpic().getId());
        epic.addSubTask(subTask);
        subTask.setId(generateID());
        allSubTasks.put(subTask.getId(), subTask);
        if (!prioritizedTasks.isEmpty())
            checkTaskTime(subTask);
        prioritizedTasks.add(subTask);
        calculateEpic(epic);
        return subTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateID());
        epics.put(epic.getId(), epic);
        if (!prioritizedTasks.isEmpty())
            checkTaskTime(epic);
        prioritizedTasks.add(epic);
        return epic;
    }

    @Override
    public ArrayList<SubTask> getSubTasksOfEpic(Epic epic) {
        return epic.subTasks;
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null)
            historyManager.add(tasks.get(id));
        else
            throw new NotFoundException("Не найдена задача под номером: " + id);
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        if (allSubTasks.get(id) != null)
            historyManager.add(allSubTasks.get(id));
        else
            throw new NotFoundException("Не найдена задача под номером: " + id);
        return allSubTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) != null)
            historyManager.add(epics.get(id));
        else
            throw new NotFoundException("Не найдена задача под номером: " + id);
        return epics.get(id);
    }

    @Override
    public void updateTask(Task task, Task updatedTask) {
        updatedTask.setId(task.getId());
        tasks.put(updatedTask.getId(), updatedTask);
        checkTaskTime(updatedTask);
        prioritizedTasks.remove(task);
        prioritizedTasks.add(updatedTask);
    }

    @Override
    public void updateSubTask(SubTask subTask, SubTask updatedSubTask) {
        updatedSubTask.setId(subTask.getId());
        allSubTasks.put(updatedSubTask.getId(), updatedSubTask);
        Epic epic = subTask.getEpic();
        epic.removeSubTask(subTask);
        epic.addSubTask(updatedSubTask);
        checkTaskTime(updatedSubTask);
        prioritizedTasks.remove(subTask);
        prioritizedTasks.add(updatedSubTask);
        calculateEpic(epic);
    }

    @Override
    public void updateEpic(Epic epic, Epic updatedEpic) {
        updatedEpic.setId(epic.getId());
        updatedEpic.subTasks.addAll(epic.subTasks);
        epics.put(updatedEpic.getId(), updatedEpic);
        checkTaskTime(updatedEpic);
        prioritizedTasks.remove(epic);
        prioritizedTasks.add(updatedEpic);
    }

    protected void calculateEpic(Epic epic) {
        int i = 0;
        int j = 0;
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        long duration = 0;
        for (SubTask subTask : epic.subTasks) {
            if (subTask.getStatus().equals(Status.DONE)) {
                i++;
            }
            if (subTask.getStatus().equals(Status.NEW)) {
                j++;
            }
            if (subTask.getStartTime().isBefore(start)) {
                start = subTask.getStartTime();
            }
            if (subTask.getEndTime().isAfter(end)) {
                end = subTask.getEndTime();
            }
            duration += subTask.getDuration().toMinutes();
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
        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(Duration.ofMinutes(duration));
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        Task task = tasks.get(id);
        tasks.remove(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public void removeSubTask(int id) {
        historyManager.remove(id);
        SubTask subTask = allSubTasks.get(id);
        Epic epic = subTask.getEpic();
        allSubTasks.remove(id);
        subTask.removeSubTask(subTask);
        calculateEpic(epic);
        prioritizedTasks.remove(subTask);
    }

    @Override
    public void removeEpic(int id) {
        historyManager.remove(id);
        Epic removedEpic = epics.get(id);
        epics.remove(id);
        removeSubTasksOfEpic(removedEpic);
        removedEpic.removeAllSubTask();
        prioritizedTasks.remove(removedEpic);
    }

    private void removeSubTasksOfEpic(Epic removedEpic) {
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

    private void checkTaskTime(Task task) {
        for (Task t : prioritizedTasks) {
            if ((task.getStartTime().isAfter(t.getStartTime())
                    && task.getStartTime().isBefore(t.getEndTime()))
                    | (task.getEndTime().isAfter(t.getStartTime())
                    && task.getEndTime().isBefore(t.getEndTime()))) {
                throw new ValidationException("Пересечение задачи " + task.getId() + " с задачей под номером " + t.getId());
            }
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}