package model;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<SubTask> subTasksOfEpic = new ArrayList<>();

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    public void addSubTask(SubTask subTask) {
        subTasksOfEpic.add(subTask);
    }

    public void removeSubTask(SubTask subTask) {
        subTasksOfEpic.remove(subTask);
    }

    public void removeAllSubTask() {
        subTasksOfEpic.clear();
    }


}
