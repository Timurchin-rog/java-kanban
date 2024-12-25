package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;
import service.memory.InMemoryTaskManager;
import service.memory.TaskManager;

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
        task = taskManager.createTask(new Task(Type.TASK, "Test Task", Status.NEW,
                "Test Task description", 10, "01.01.2000, 12:00"));
        epic = taskManager.createEpic(new Epic(Type.EPIC, "Test Epic", "Test Epic description"));
        subTask = taskManager.createSubTask(new SubTask(Type.SUBTASK, "Test SubTask", Status.NEW,
                "Test SubTask description", 10, "01.01.2000, 12:00", epic));
    }

    @Test
    @DisplayName("должен получать заполненную историю просмотров")
    void shouldFillBrowsingHistory() {
        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getTask(task.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic.getId());
        assertNotNull(historyManager.getHistory(), "История просмотров не заполнена");
        assertEquals(historyManager.getHistory().size(), 3, "Присутствуют дубликаты");
        ArrayList<Task> browsingHistory = new ArrayList<>();
        browsingHistory.add(epic);
        browsingHistory.add(subTask);
        browsingHistory.add(task);
        assertEquals(historyManager.getHistory(), browsingHistory, "Нарушен порядок посмотров в истории");
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