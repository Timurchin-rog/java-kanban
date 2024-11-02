package service;

import org.junit.jupiter.api.DisplayName;
import service.history.InMemoryHistoryManager;

@DisplayName("TaskManager")
class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    public InMemoryTaskManager createManager() {
        historyManager = new InMemoryHistoryManager();
        return new InMemoryTaskManager(historyManager);
    }

}