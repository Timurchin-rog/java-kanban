package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Утилитарный класс Managers")
class ManagersTest {
    @Test
    @DisplayName("должен возвращать экземпляры менеджеров")
    void shouldManagersReturnObjects() {
        TaskManager taskManager1 = Managers.getDefault();
        InMemoryHistoryManager historyManager1 = new InMemoryHistoryManager();
        TaskManager taskManager2 = new InMemoryTaskManager(historyManager1);
        assertEquals(taskManager1.getClass(), taskManager2.getClass(), "Классы экземпляров менеджеров не совпадают");

        HistoryManager historyManager2 = Managers.getDefaultHistory();
        assertEquals(historyManager1.getClass(), historyManager2.getClass(), "Классы экземпляров менеджеров не совпадают");
    }
}