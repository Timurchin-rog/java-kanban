package model;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(int id, Type type, String name, Status status, String description) {
        super(id, type, name, status, description);
    }

    public Epic(Type type, String name, Status status, String description) {
        super(type, name, status, description);
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public void removeAllSubTask() {
        subTasks.clear();
    }

}
