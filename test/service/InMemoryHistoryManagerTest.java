package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("HistoryManager")
class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    TaskManager taskManager;
    Task task;
    Epic epic;
    SubTask subTask;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
    }

    @Test
    @DisplayName("должен получать заполненную историю просмотров")
    void shouldFillBrowsingHistory() {
        historyManager.add(task);
        historyManager.add(subTask);
        historyManager.add(epic);
        assertNotNull(historyManager.getHistory(), "История просмотров не заполнена");
        ArrayList<Task> browsingHistory = new ArrayList<>();
        browsingHistory.add(task);
        browsingHistory.add(subTask);
        browsingHistory.add(epic);
        assertEquals(historyManager.getHistory(), browsingHistory, "История просмотров неверна");
    }
}