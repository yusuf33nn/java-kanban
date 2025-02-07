package models;

import enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private long id;
    private String name;
    private String description;
    private final TaskType taskType;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;



    public Task(String name,
                String description,
                TaskType taskType,
                LocalDateTime startTime,
                Duration duration) {
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(long id,
                String name,
                String description,
                TaskType taskType,
                TaskStatus status,
                LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    public TaskType getTaskType() {
        return taskType;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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
        Task copy = new Task(this.name, this.description, this.taskType, this.startTime, this.duration);
        copy.setId(this.id);
        copy.setStatus(this.status);
        return copy;
    }
//
//    @Override
//    public String toString() {
//
//        return "%d,%s,%s,%s,%s,%s,%d"
//                .formatted(id, taskType.toString(), name, status.toString(), description, startTime, durationForRecord);
//    }

    @Override
    public String toString() {
        var durationForRecord = Optional.ofNullable(duration).map(Duration::toMinutes).orElse(0L);
        return "Task{" +
                "id=" + id +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", duration=" + durationForRecord +
                '}';
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }
}
