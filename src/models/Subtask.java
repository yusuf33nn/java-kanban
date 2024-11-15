package models;

import enums.TaskStatus;
import models.Epic;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
    }

    @Override
    public Subtask copy() {
        Subtask copy = new Subtask(this.getName(), this.getDescription());
        copy.setId(this.getId());
        copy.setEpic(this.getEpic());
        copy.setStatus(this.getStatus());
        return copy;

    }
}
