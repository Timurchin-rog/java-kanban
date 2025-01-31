package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import service.history.InMemoryHistoryManager;
import service.memory.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

@DisplayName("Предок различных реализаций менеджеров")
abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    InMemoryHistoryManager historyManager;
    Task task;
    Subtask subtask;
    Epic epic;

    protected abstract T createManager();

    @BeforeEach
    void init() {
        manager = createManager();
        task = manager.createTask(new Task(Type.TASK, "Test Task", "NEW",
                "Test Task description", Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 0, 0)));
        epic = manager.createEpic(new Epic(Type.EPIC, "Test Epic", "Test Epic description"));
        subtask = manager.createSubtask(new Subtask(Type.SUBTASK, "Test Subtask", "DONE",
                "Test Subtask description", Duration.ofMinutes(10),
                LocalDateTime.of(2000, 1, 1, 12, 0, 0), epic.getId()));
    }
}
