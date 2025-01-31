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

@DisplayName("Подзадача")
class SubtaskTest {
    TaskManager taskManager;
    Epic epic;
    Subtask subtask;
    int subTaskId;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        epic = taskManager.createEpic(new Epic(Type.EPIC, "Test Epic", "Test Epic description"));
        subtask = taskManager.createSubtask(new Subtask(Type.SUBTASK,
                "Test Subtask", "NEW", "Test Subtask description", Duration.ofMinutes(10),
                LocalDateTime.of(2000, 1, 1, 12, 0, 0), epic.getId()));
        subTaskId = subtask.getId();
    }

    @Test
    @DisplayName("Должна совпадать со своей копией")
    void shouldEqualsWithCopy() {
        final Subtask savedSubtask = taskManager.getSubtask(subTaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
    }

    @Test
    @DisplayName("Должна совпадать со своей копией из хеш-таблицы")
    void shouldEqualsWithCopyFromHashMap() {
        final HashMap<Integer, Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(subTaskId), "Подзадачи не совпадают");
    }
}