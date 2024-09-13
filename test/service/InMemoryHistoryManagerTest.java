package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("HistoryManager")
class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();
    @Test
    @DisplayName("должен заполнять историю просмотров и получать её")
    void shouldFillBrowsingHistoryAndGetIt() {
        Task task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        Epic epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Test SubTask", Status.NEW, "Test SubTask description", epic));
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