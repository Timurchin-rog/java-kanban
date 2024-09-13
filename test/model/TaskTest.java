package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Задача")
class TaskTest {
    @Test
    @DisplayName("должна совпадать со своей копией")
    void shouldTaskEqualsWithCopy() {
        TaskManager taskManager = Managers.getDefault();
        Task task = taskManager.createTask(new Task("Test Task", Status.NEW, "Test Task description"));
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task.getId(), savedTask.getId(), "Задачи не совпадают - разные ID");
        assertEquals(task.getName(), savedTask.getName(), "Задачи не совпадают - разные названия");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Задачи не совпадают - разные статусы");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Задачи не совпадают - разные описания");

        final HashMap<Integer, Task> tasks = taskManager.printAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.getId(), tasks.get(taskId).getId(), "Задачи не совпадают - разные ID");
        assertEquals(task.getName(), tasks.get(taskId).getName(), "Задачи не совпадают - разные названия");
        assertEquals(task.getStatus(), tasks.get(taskId).getStatus(), "Задачи не совпадают - разные статусы");
        assertEquals(task.getDescription(), tasks.get(taskId).getDescription(), "Задачи не совпадают - разные описания");
    }
}