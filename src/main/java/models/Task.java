package models;

import enums.TaskStatus;

import java.util.Objects;

public class Task {
    private long id;
    private String name;
    private String description;
    private final TaskType taskType;
    private TaskStatus status;


    public Task(String name, String description, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        this.status = TaskStatus.NEW;
    }

    public TaskType getTaskType() {
        return taskType;
    }



    public Task(long id, String name, String description, TaskType taskType, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Task copy() {
        Task copy = new Task(this.name, this.description, this.taskType);
        copy.setId(this.id);
        copy.setStatus(this.status);
        return copy;
    }

    @Override
    public String toString() {
        return "%d,%s,%s,%s,%s".formatted(id, taskType.toString(), name, status.toString(), description);
    }
}
