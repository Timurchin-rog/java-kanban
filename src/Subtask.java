public class Subtask extends Task {
    Epic epic;

    public Subtask(String name, Status status, String description) {
        super(name, status, description);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
