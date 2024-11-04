package model;

public class SubTask extends Task {
    Epic epic;

    public SubTask(int id, Type type, String name, Status status, String description, Epic epic) {
        super(id, type, name, status, description);
        this.epic = epic;
    }

    public SubTask(Type type, String name, Status status, String description, Epic epic) {
        super(type, name, status, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void removeSubTask(SubTask subTask) {
        epic.subTasks.remove(subTask);
    }
}
