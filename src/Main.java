import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.createTask(new Task("Задача 1", Status.NEW, "Погулять"));
        Task task2 = taskManager.createTask(new Task("Задача 2", Status.IN_PROGRESS, "Бегать"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", Status.NEW, "Уборка"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", Status.NEW, "ДЗ"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Подзадача 1", Status.DONE, "Мыть пол", epic1));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Подзадача 2", Status.IN_PROGRESS, "Протирать пыль", epic1));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Подзадача 3", Status.DONE, "Программирование", epic2));
        System.out.println(taskManager.printAllTasks());
        System.out.println(taskManager.printAllEpics());
        System.out.println(taskManager.printAllSubTasks());
        System.out.println();
        System.out.println(taskManager.getSubTasksOfEpic(epic1));
        System.out.println();
        System.out.println(taskManager.getSubTasksOfEpic(epic2));
        taskManager.updateTask(task1, "Новая задача 1", Status.NEW, "Протирать пыль");
        System.out.println(taskManager.printAllTasks());
        taskManager.updateEpic(epic1, "Новый эпик 1", "Заниматься физкультурой");
        System.out.println(taskManager.printAllEpics());
        System.out.println(taskManager.getSubTasksOfEpic(epic1));
        taskManager.updateSubTask(epic2, subTask3, "Новая подзадача 1 эпика 2", Status.DONE, "Пылесосить");
        System.out.println();
        System.out.println(taskManager.getSubTasksOfEpic(epic1));
        System.out.println();
        System.out.println(taskManager.printAllSubTasks());
        System.out.println(taskManager.printAllEpics());
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getSubTask(6));
        taskManager.removeTask(1);
        System.out.println(taskManager.getTask(1));
        taskManager.removeEpic(4);
        System.out.println(taskManager.getEpic(4));
        taskManager.removeSubTask(6);
        System.out.println(taskManager.getSubTask(6));
        System.out.println(taskManager.getSubTasksOfEpic(epic1));
        System.out.println(taskManager.getSubTasksOfEpic(epic2));
        System.out.println(taskManager.printAllSubTasks());
        taskManager.removeAllSubTasks(epic2);
        System.out.println();
        System.out.println(taskManager.printAllSubTasks());
        System.out.println();
        System.out.println(taskManager.getSubTasksOfEpic(epic2));
    }

}
