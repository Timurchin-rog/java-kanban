package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

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
        task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        taskId = task.getId();
    }

    @Test
    @DisplayName("должна совпадать со своей копией")
    void shouldEqualsWithCopy() {
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    @Test
    @DisplayName("должна совпадать со своей копией из хеш-таблицы")
    void shouldEqualsWithCopyFromHashMap() {
        final HashMap<Integer, Task> tasks = taskManager.printAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskId), "Задачи не совпадают");
    }
}