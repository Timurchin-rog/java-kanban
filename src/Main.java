public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW, "Описание"));
        Task task1 = taskManager.createTask(new Task("Новая задача 1", Status.NEW, "Описание 1"));
        Task epic = taskManager.createEpic(new Epic("Новая задача 10", Status.NEW, "Описание 10"));
        taskManager.printAllTasks();
        taskManager.printAllEpics();
    }

}
