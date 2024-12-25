import model.*;
import service.Managers;
import service.memory.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.createEpic(new Epic(Type.EPIC, "Test Epic", "Test Epic description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(Type.SUBTASK, "SubTask name",
                Status.NEW, "SubTask description", 60, "01.09.2000, 08:00", epic));
        Task task = taskManager.createTask(new Task(Type.TASK, "Task name", Status.NEW,
                "Task description", 10, "01.01.2000, 12:00"));
        SubTask subTask1 = (new SubTask(Type.SUBTASK, "SubTask name",
                Status.NEW, "SubTask description", 45, "01.01.2984, 08:00", epic));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(Type.SUBTASK, "SubTask name",
                Status.NEW, "SubTask description", 20, "01.09.1995, 08:00", epic));
        taskManager.updateSubTask(subTask, subTask1);
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
