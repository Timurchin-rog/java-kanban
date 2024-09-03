package model;

public class SubTask extends Task {
    Epic epic;

    public SubTask(String name, Status status, String description, Epic epic) {
        super(name, status, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void removeSubTask(SubTask subTask) {
        epic.subTasksOfEpic.remove(subTask);
    }
}
