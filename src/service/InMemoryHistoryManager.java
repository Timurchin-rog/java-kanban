package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> browsingHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        browsingHistory.add(task);
        if (browsingHistory.size() >= 11) {
            browsingHistory.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return browsingHistory;
    }
}
