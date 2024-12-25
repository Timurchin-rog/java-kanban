package service;

import model.Status;
import model.SubTask;
import model.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;
import service.history.InMemoryHistoryManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

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
        SubTask subTask1 = manager.createSubTask(new SubTask(Type.SUBTASK, "Test SubTask 1", Status.DONE,
                "Test SubTask description 1", 50, "09.01.2010, 18:00", epic));
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(loadManager.printAllSubTasks().size(), 2, "Подзадачи не загрузились из файла");
        assertEquals(task, loadManager.tasks.get(task.getId()), "Задачи не совпадают");
        assertEquals(subTask1, loadManager.allSubTasks.get(subTask1.getId()), "Подзадачи не совпадают");
    }
}

