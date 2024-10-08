package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> browsingHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        browsingHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return browsingHistory;
    }
}




