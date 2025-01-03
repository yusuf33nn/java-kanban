package models;

import enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Epic extends Task {
    private LocalDateTime endTime;
    private final List<Subtask> subtasks;

    public Epic(String name,
                String description,
                LocalDateTime startTime,
                Duration duration) {
        super(name, description, TaskType.EPIC, startTime, duration);
        this.subtasks = new ArrayList<>();
    }

    public Epic(long id,
                String name,
                String description,
                TaskStatus status,
                LocalDateTime startTime,
                Duration duration) {
        super(id, name, description, TaskType.EPIC, status, startTime, duration);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void calculateEpic() {
        calculateEpicStatus();
        calculateEpicTimes();
    }

    private void calculateEpicStatus() {
        int newTasks = (int)subtasks.stream().filter(subtask -> subtask.getStatus() == TaskStatus.NEW).count();

        if (subtasks.isEmpty() || newTasks == subtasks.size()) {
            this.setStatus(TaskStatus.NEW);
            return;
        }

        int doneTasks = (int)subtasks.stream().filter(subtask -> subtask.getStatus() == TaskStatus.DONE).count();

        if (doneTasks == subtasks.size()) {
            this.setStatus(TaskStatus.DONE);
        } else {
            this.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void calculateEpicTimes() {
        var duration = subtasks.stream().map(Task::getDuration).reduce(Duration.ZERO, Duration::plus);
        this.setDuration(duration);

        subtasks.stream()
                .map(Task::getStartTime)
                .min(Comparator.naturalOrder())
                .ifPresent(this::setStartTime);

        subtasks.stream()
                .map(Task::getEndTime)
                .max(Comparator.naturalOrder())
                .ifPresent(max -> this.endTime = max);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateSubtasks(Subtask updatedSubtask) {
        int indexOfSubtaskToUpdate = subtasks.indexOf(updatedSubtask);
        subtasks.set(indexOfSubtaskToUpdate, updatedSubtask);
    }

    @Override
    public Epic copy() {
        Epic copy = new Epic(this.getName(), this.getDescription(), this.getStartTime(), this.getDuration());
        copy.setId(this.getId());
        copy.setStatus(this.getStatus());
        for (Subtask subtask : this.getSubtasks()) {
            copy.addSubtask(subtask.copy());
        }
        return copy;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
