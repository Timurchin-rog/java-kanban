package service.history;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task item;
        Node next;
        Node prev;

        private Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private HashMap<Integer, Node> historyMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        Node node = historyMap.get(task.getId());
        removeNode(node);
        linkedLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node current = last;
        while (current != null) {
            historyList.add(current.item);
            current = current.prev;
        }
        return historyList;
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
    }

    private void linkedLast(Task task) {
        Node l = last;
        Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        historyMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node != null && historyMap.size() > 2) {
            if (node == first) {
                first.next.prev = null;
            } else if (node == last) {
                last.prev.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
        }
    }
}




