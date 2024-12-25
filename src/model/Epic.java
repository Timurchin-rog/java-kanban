package model;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(int id, Type type, String name, Status status, String description,
                int duration, String startTime) {
        super(id, type, name, status, description, duration, startTime);
    }

    public Epic(Type type, String name, String description) {
        super(type, name, description);
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

    @Override
    public Epic getEpic() {
        return epic;
    }
}
