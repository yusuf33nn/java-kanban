package history;

import models.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(long id);

    void removeAllHistory();

    List<Task> getHistory();
}
