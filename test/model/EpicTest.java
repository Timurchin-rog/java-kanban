package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Эпик")
class EpicTest {
    TaskManager taskManager;
    Epic epic;
    int epicId;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        epicId = epic.getId();
    }

    @Test
    @DisplayName("должен совпадать со своей копией")
    void shouldEqualsWithCopy() {
        final Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    @DisplayName("должен совпадать со своей копией из хеш-таблицы")
    void shouldEqualsWithCopyFromHashMap() {
        final HashMap<Integer, Epic> epics = taskManager.printAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(epicId), "Эпики не совпадают");
    }
}