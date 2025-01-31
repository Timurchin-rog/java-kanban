package service;

import model.Subtask;
import model.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;
import service.history.InMemoryHistoryManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("FileManager")
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File file = new File("Test File.csv");

    @Override
    public FileBackedTaskManager createManager() {
        historyManager = new InMemoryHistoryManager();
        return new FileBackedTaskManager(historyManager, file);
    }

    @Test
    @DisplayName("Должен загружать задачи из пустого файла")
    void shouldLoadTasksFromEmptyFile() {
        manager.removeTask(task.getId());
        manager.removeEpic(epic.getId());
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(loadManager.getAllTasks().size(), 0, "Из-за пустого файла всё упало");
    }

    @Test
    @DisplayName("Должен загружать задачи из файла")
    void shouldLoadTasksFromFile() {
        Subtask subtask1 = manager.createSubtask(new Subtask(Type.SUBTASK, "Test Subtask 1", "DONE",
                "Test Subtask description 1", Duration.ofMinutes(50), LocalDateTime.of(2010, 1, 9, 18, 0, 0), epic.getId()));
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(loadManager.getAllSubtasks().size(), 2, "Подзадачи не загрузились из файла");
        assertEquals(task, loadManager.getAllTasks().get(task.getId()), "Задачи не совпадают");
        assertEquals(subtask1, loadManager.getAllSubtasks().get(subtask1.getId()), "Подзадачи не совпадают");
    }
}

