package model;

public class SubTask extends Task {

    public SubTask(int id, Type type, String name, Status status, String description,
                   int duration, String startTime, Epic epic) {
        super(id, type, name, status, description, duration, startTime);
        this.epic = epic;
    }

    public SubTask(Type type, String name, Status status, String description,
                   int duration, String startTime, Epic epic) {
        super(type, name, status, description, duration, startTime);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public Integer getEpicId() {
        return epic.getId();
    }

    public void removeSubTask(SubTask subTask) {
        epic.subTasks.remove(subTask);
    }
}
