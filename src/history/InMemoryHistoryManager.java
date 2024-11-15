package history;

import models.Task;
import utils.CopyUtils;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (taskHistory.size() == 10) {
            taskHistory.removeFirst();
        }
        Task copy = CopyUtils.copyForHistory(task);
        taskHistory.add(copy);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
