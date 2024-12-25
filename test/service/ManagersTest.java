package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;
import service.memory.InMemoryTaskManager;
import service.memory.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Утилитарный класс Managers")
class ManagersTest {
    TaskManager taskManager1;
    TaskManager taskManager2;
    InMemoryHistoryManager historyManager1;
    HistoryManager historyManager2;

    @BeforeEach
    void init() {
        taskManager1 = Managers.getDefault();
        taskManager2 = new InMemoryTaskManager(historyManager1);
        historyManager1 = new InMemoryHistoryManager();
        historyManager2 = Managers.getDefaultHistory();
    }

    @Test
    @DisplayName("должен возвращать объект TaskManager")
    void shouldReturnObjectOfTaskManager() {
        assertEquals(taskManager1.getClass(), taskManager2.getClass(), "Классы экземпляров менеджеров не совпадают");
    }

    @Test
    @DisplayName("должен возвращать объект HistoryManager")
    void shouldReturnObjectOfHistoryManager() {
        assertEquals(historyManager1.getClass(), historyManager2.getClass(), "Классы экземпляров менеджеров не совпадают");
    }
}