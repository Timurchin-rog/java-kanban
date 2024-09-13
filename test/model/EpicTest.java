package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Эпик")
class EpicTest {
    @Test
    @DisplayName("должен совпадать со своей копией")
    void shouldEpicEqualsWithCopy() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.createEpic(new Epic("Test Epic", Status.NEW, "Test Epic description"));
        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic.getId(), savedEpic.getId(), "Эпики не совпадают - разные ID");
        assertEquals(epic.getName(), savedEpic.getName(), "Эпики не совпадают - разные названия");
        assertEquals(epic.getStatus(), savedEpic.getStatus(), "Эпики не совпадают - разные статусы");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Эпики не совпадают - разные описания");

        final HashMap<Integer, Epic> epics = taskManager.printAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic.getId(), epics.get(epicId).getId(), "Эпики не совпадают - разные ID");
        assertEquals(epic.getName(), epics.get(epicId).getName(), "Эпики не совпадают - разные названия");
        assertEquals(epic.getStatus(), epics.get(epicId).getStatus(), "Эпики не совпадают - разные статусы");
        assertEquals(epic.getDescription(), epics.get(epicId).getDescription(), "Эпики не совпадают - разные описания");
    }
}