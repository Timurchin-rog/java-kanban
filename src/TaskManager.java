import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Subtask> subtasks;
    HashMap<Integer, Epic> epics;
    private HashMap<Integer, Task> tasks;
    int idGen = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
    }

    private int generateID() {
        return ++idGen;
    }

    public void printAllTasks() {
        for (int i : tasks.keySet()) {
            if (!tasks.isEmpty() && tasks.get(i) == null) {
                break;
            } else {
                System.out.println(tasks.toString());
            }
        }
    }
    public void printAllSubtasks() {
        for (int i : subtasks.keySet()) {
            if (!subtasks.isEmpty() && subtasks.get(i) == null) {
                break;
            } else {
                System.out.println(subtasks.toString());
            }
        }
    }
    public void printAllEpics() {
        for (int i : epics.keySet()) {
            if (!epics.isEmpty() && epics.get(i) == null) {
                break;
            } else {
                System.out.println(epics.toString());
            }
        }
    }
    public void removeAllTasks() {
        tasks.clear();
    }
    public Task get(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generateID());
        tasks.put(task.getId(), task);
        return task;
    }
    public Subtask createSubtask(Subtask task) {
        task.setId(generateID());
        subtasks.put(task.getId(), task);
        return task;
    }
    public Epic createEpic(Epic task) {
        task.setId(generateID());
        epics.put(task.getId(), task);
        return task;
    }
    public void correctTask() {

    }
    public void removeTask() {

    }



}
