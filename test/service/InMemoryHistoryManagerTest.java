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
        taskManager.getTask(task.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic.getId());
        assertNotNull(historyManager.getHistory(), "История просмотров не заполнена");
        ArrayList<Task> browsingHistory = new ArrayList<>();
        browsingHistory.add(epic);
        browsingHistory.add(subTask);
        browsingHistory.add(task);
        assertEquals(historyManager.getHistory(), browsingHistory, "История просмотров неверна");
    }

    @Test
    @DisplayName("должен удалять задачу из истории просмотров")
    void shouldRemoveTaskFromBrowsingHistory() {
        taskManager.getTask(task.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic.getId());
        historyManager.remove(task.getId());
        assertEquals(historyManager.getHistory().size(), 2, "Задача не удалена");
    }
}