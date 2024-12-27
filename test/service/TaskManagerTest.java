package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.InMemoryHistoryManager;
import service.memory.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Предок различных реализаций менеджеров")
abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    InMemoryHistoryManager historyManager;
    Task task;
    SubTask subTask;
    Epic epic;

    protected abstract T createManager();

    @BeforeEach
    void init() {
        manager = createManager();
        task = manager.createTask(new Task(Type.TASK, "Test Task", Status.NEW,
                "Test Task description", 10, "12.01.2105, 12:00"));
        epic = manager.createEpic(new Epic(Type.EPIC, "Test Epic", "Test Epic description"));
        subTask = manager.createSubTask(new SubTask(Type.SUBTASK, "Test SubTask", Status.DONE,
                "Test SubTask description", 10, "01.01.2000, 12:00", epic));
    }
}
