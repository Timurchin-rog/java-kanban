package model;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, Status status, String description) {
        super(name, status, description);
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
