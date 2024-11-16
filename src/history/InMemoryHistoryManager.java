package history;

import models.Task;
import utils.CopyUtils;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskHistory = new LinkedList<>();

    private static final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (taskHistory.size() == MAX_SIZE) {
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
