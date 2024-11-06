package models;

import enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void calculateEpicStatus() {
        int newTasks = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.NEW) {
                newTasks++;
            }
        }
        if (subtasks.isEmpty() || newTasks == subtasks.size()) {
            this.setStatus(TaskStatus.NEW);
            return;
        }

        int doneTasks = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.DONE) {
                doneTasks++;
            }
        }

        if (doneTasks == subtasks.size()) {
            this.setStatus(TaskStatus.DONE);
        } else {
            this.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateSubtasks(Subtask updatedSubtask) {
        int indexOfSubtaskToUpdate = subtasks.indexOf(updatedSubtask);
        subtasks.set(indexOfSubtaskToUpdate, updatedSubtask);
    }
}
