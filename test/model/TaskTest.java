package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.memory.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Задача")
class TaskTest {
    TaskManager taskManager;
    Task task;
    int taskId;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        task = taskManager.createTask(new Task(Type.TASK, "Test Task", "NEW",
                "Test Task description", Duration.ofMinutes(10),
                LocalDateTime.of(2000, 1, 1, 12, 0, 0)));
        taskId = task.getId();
    }

    @Test
    @DisplayName("Должна совпадать со своей копией")
    void shouldEqualsWithCopy() {
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    @DisplayName("Должна совпадать со своей копией из хеш-таблицы")
    void shouldEqualsWithCopyFromHashMap() {
        final HashMap<Integer, Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskId), "Задачи не совпадают");
    }
}