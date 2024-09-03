import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, SubTask> subTasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Task> tasks;
    int idGen = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateID() {
        return ++idGen;
    }

    public Object printAllTasks() {
        return tasks;
    }

    public Object printAllSubTasks() {
        return subTasks;
    }

    public Object printAllEpics() {
        return epics;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
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
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateID());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public ArrayList<SubTask> getSubTasksOfEpic(Epic epic) {
        return epic.subTasksOfEpic;
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
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
        calculateStatusOfEpic(epic);


    }

    public void updateEpic(Epic epic, String name, String description) {
        epic.setName(name);
        epic.setDescription(description);
    }

    public void calculateStatusOfEpic(Epic epic) {
        int i = 0;
        int j = 0;
        for (SubTask subTask : epic.subTasksOfEpic) {
            if (subTask.getStatus().equals(Status.DONE)) {
                i++;
            }
            if (subTask.getStatus().equals(Status.NEW)) {
                j++;
            }
        }
        if (epic.subTasksOfEpic.size() == i) {
            epic.setStatus(Status.DONE);
        }
        if (epic.subTasksOfEpic.size() == j) {
            epic.setStatus(Status.NEW);
        }
        if (!(epic.subTasksOfEpic.size() == i) && !(epic.subTasksOfEpic.size() == j)) {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        System.out.println(subTask);
        subTasks.remove(id);
        subTask.removeSubTask(subTask);
    }

    public void removeEpic(int id) {
        Epic removedEpic = epics.get(id);
        epics.remove(id);
        removedEpic.removeAllSubTask();
    }


}
