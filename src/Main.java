import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = taskManager.createTask(new Task("Задача 1", Status.NEW, "Погулять"));
        Task task2 = taskManager.createTask(new Task("Задача 2", Status.IN_PROGRESS, "Бегать"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", Status.NEW, "Уборка"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", Status.NEW, "ДЗ"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Подзадача 1", Status.DONE, "Мыть пол", epic1));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Подзадача 2", Status.IN_PROGRESS, "Протирать пыль", epic1));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Подзадача 3", Status.DONE, "Программирование", epic2));
        taskManager.getEpic(3);
        System.out.println(taskManager.getHistory());
        taskManager.getSubTask(5);
        System.out.println(taskManager.getHistory());
        taskManager.getSubTask(5);
        taskManager.getSubTask(6);
        taskManager.getEpic(3);
        taskManager.removeSubTask(6);
        System.out.println(taskManager.getHistory());
    }
}
