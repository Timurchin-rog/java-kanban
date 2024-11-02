package service;

import model.Status;
import model.Task;
import model.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;
import service.history.InMemoryHistoryManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest {

    File file = new File("Test File.csv");

    @Override
    public FileBackedTaskManager createManager() {
        historyManager = new InMemoryHistoryManager();
        return new FileBackedTaskManager(historyManager, file);
    }

    @Test
    @DisplayName("загружать задачи из пустого файла")
    void shouldLoadTasksFromEmptyFile() {
        manager.removeTask(task.getId());
        manager.removeEpic(epic.getId());
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(loadManager.printAllTasks().size(), 0, "Из-за пустого файла всё упало");
    }

    @Test
    @DisplayName("должен загружать задачи из файла")
    void shouldLoadTasksFromFile() {
        Task task1 = manager.createTask(new Task(Type.TASK, "Test Task 1", Status.NEW, "Test Task 1 description"));
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(loadManager.printAllTasks().size(), 2, "Задачи не загрузились из файла");
        assertEquals(task, loadManager.tasks.get(task.getId()), "Задачи не совпадают");
        assertEquals(task1, loadManager.tasks.get(task1.getId()), "Задачи не совпадают");
    }
}

