import model.*;
import service.file.FileBackedTaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(new File("File.csv"));

        Task task1 = fileManager.createTask(new Task(Type.TASK, "Задача 1", Status.NEW, "Погулять"));
        Task task2 = fileManager.createTask(new Task(Type.TASK, "Задача 2", Status.IN_PROGRESS, "Бегать"));
        Epic epic1 = fileManager.createEpic(new Epic(Type.EPIC, "Эпик 1", Status.NEW, "Уборка"));
        Epic epic2 = fileManager.createEpic(new Epic(Type.EPIC, "Эпик 2", Status.NEW, "ДЗ"));
        SubTask subTask1 = fileManager.createSubTask(new SubTask(Type.SUBTASK,
                "Подзадача 1", Status.DONE, "Мыть пол", epic1));
        SubTask subTask2 = fileManager.createSubTask(new SubTask(Type.SUBTASK,
                "Подзадача 2", Status.IN_PROGRESS, "Протирать пыль", epic1));
        SubTask subTask3 = fileManager.createSubTask(new SubTask(Type.SUBTASK,
                "Подзадача 3", Status.DONE, "Программирование", epic2));

        fileManager.updateSubTask(subTask3, "Обновлённая подзадача", Status.DONE, "Жениться");
        fileManager.removeSubTask(7);
        fileManager.removeEpic(3);
    }
}
