package models;

import enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name,
                   String description,
                   LocalDateTime startTime,
                   Duration duration) {
        super(name, description, TaskType.SUBTASK, startTime, duration);
    }

    public Subtask(long id,
                   String name,
                   String description,
                   TaskStatus status,
                   Epic epic,
                   LocalDateTime startTime,
                   Duration duration) {
        super(id, name, description, TaskType.SUBTASK, status, startTime, duration);
        this.epic = epic;
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
        Subtask copy = new Subtask(this.getName(), this.getDescription(), this.getStartTime(), this.getDuration());
        copy.setId(this.getId());
        copy.setEpic(this.getEpic());
        copy.setStatus(this.getStatus());
        return copy;
    }

//    @Override
//    public String toString() {
//        return super.toString();
//    }
}
