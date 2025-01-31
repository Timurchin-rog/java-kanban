package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;
import service.memory.InMemoryTaskManager;
import service.memory.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("HistoryManager")
class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    TaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        task = taskManager.createTask(new Task(Type.TASK, "Test Task", "NEW",
                "Test Task description", Duration.ofMinutes(10),
                LocalDateTime.of(2000, 1, 1, 12, 0, 0)));
        epic = taskManager.createEpic(new Epic(Type.EPIC, "Test Epic", "Test Epic description"));
        subtask = taskManager.createSubtask(new Subtask(Type.SUBTASK, "Test Subtask", "NEW",
                "Test Subtask description", Duration.ofMinutes(10),
                LocalDateTime.of(2000, 1, 1, 12, 0, 0), epic.getId()));
    }

    @Test
    @DisplayName("Должен получать заполненную историю просмотров")
    void shouldFillBrowsingHistory() {
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.getEpic(epic.getId());
        assertNotNull(historyManager.getHistory(), "История просмотров не заполнена");
        assertEquals(historyManager.getHistory().size(), 3, "Присутствуют дубликаты");
        ArrayList<Task> browsingHistory = new ArrayList<>();
        browsingHistory.add(epic);
        browsingHistory.add(subtask);
        browsingHistory.add(task);
        assertEquals(historyManager.getHistory(), browsingHistory, "Нарушен порядок посмотров в истории");
    }

    @Test
    @DisplayName("Должен удалять задачу из истории просмотров")
    void shouldRemoveTaskFromBrowsingHistory() {
        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.getEpic(epic.getId());
        historyManager.remove(task.getId());
        assertEquals(historyManager.getHistory().size(), 2, "Задача не удалена");
    }
}