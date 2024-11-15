package utils;

import models.Epic;
import models.Subtask;
import models.Task;

public class CopyUtils{

    public static Task copyForHistory(Task task) {

        if (task instanceof Subtask subtask) {
            return subtask.copy();
        } else if (task instanceof Epic epic) {
            return epic.copy();
        } else {
            return task.copy();
        }
    }
}
