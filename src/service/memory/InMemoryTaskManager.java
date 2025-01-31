package service.memory;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Subtask> allSubtasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Task> tasks;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final HistoryManager historyManager;
    private int idGen = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.allSubtasks = new HashMap<>();
    }

    private int generateID() {
        return ++idGen;
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Subtask> getAllSubtasks() {
        return allSubtasks;
    }

    @Override
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        allSubtasks.clear();
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
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subtask.setId(generateID());
        allSubtasks.put(subtask.getId(), subtask);
        if (!prioritizedTasks.isEmpty())
            checkTaskTime(subtask);
        prioritizedTasks.add(subtask);
        calculateEpic(epic);
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        calculateEpic(epic);
        epic.setId(generateID());
        epics.put(epic.getId(), epic);
        if (!prioritizedTasks.isEmpty())
            checkTaskTime(epic);
        prioritizedTasks.add(epic);
        return epic;
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.subtasks;
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            throw new NotFoundException(String.format("Не найдена задача под номером: %d", id));
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (allSubtasks.get(id) != null) {
            historyManager.add(allSubtasks.get(id));
            return allSubtasks.get(id);
        } else {
            throw new NotFoundException(String.format("Не найдена задача под номером: %d", id));
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            throw new NotFoundException(String.format("Не найдена задача под номером: %d", id));
        }
    }

    @Override
    public void updateTask(int id, Task updatedTask) {
        prioritizedTasks.remove(getTask(id));
        updatedTask.setId(id);
        tasks.put(updatedTask.getId(), updatedTask);
        checkTaskTime(updatedTask);
        prioritizedTasks.add(updatedTask);
    }

    @Override
    public void updateSubtask(int id, Subtask updatedSubtask) {
        Subtask subtask = getSubtask(id);
        updatedSubtask.setId(id);
        allSubtasks.put(updatedSubtask.getId(), updatedSubtask);
        Epic epic = epics.get(updatedSubtask.getEpicId());
        epic.removeSubtask(subtask);
        epic.addSubtask(updatedSubtask);
        checkTaskTime(updatedSubtask);
        prioritizedTasks.remove(subtask);
        prioritizedTasks.add(updatedSubtask);
        calculateEpic(epic);
    }

    @Override
    public void updateEpic(int id, Epic updatedEpic) {
        Epic epic = getEpic(id);
        updatedEpic.setId(id);
        updatedEpic.subtasks.addAll(epic.subtasks);
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
        for (Subtask subTask : epic.subtasks) {
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
        if (epic.subtasks.size() == i) {
            epic.setStatus(Status.DONE);
        }
        if (epic.subtasks.size() == j || epic.subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
        }
        if (!(epic.subtasks.size() == i) && !(epic.subtasks.size() == j)) {
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
    public void removeSubtask(int id) {
        historyManager.remove(id);
        Subtask subTask = allSubtasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        allSubtasks.remove(id);
        epic.subtasks.remove(subTask);
        calculateEpic(epic);
        prioritizedTasks.remove(subTask);
    }

    @Override
    public void removeEpic(int id) {
        historyManager.remove(id);
        Epic removedEpic = epics.get(id);
        epics.remove(id);
        removeSubtasksOfEpic(removedEpic);
        removedEpic.removeAllSubtask();
        prioritizedTasks.remove(removedEpic);
    }

    private void removeSubtasksOfEpic(Epic removedEpic) {
        for (int i = 0; i < removedEpic.subtasks.size(); i++) {
            if (allSubtasks.get(removedEpic.subtasks.get(i).getId()) != null) {
                allSubtasks.remove(removedEpic.subtasks.get(i).getId());
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
                throw new ValidationException(String.format("Пересечение задачи %d с задачей под номером %d", task.getId(), t.getId()));
            }
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}