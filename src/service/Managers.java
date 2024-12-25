package service;

import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;
import service.memory.InMemoryTaskManager;
import service.memory.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
