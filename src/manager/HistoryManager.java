package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager { // 2 метода add и getHistory

    void add(Task task);

    List<Task> getHistory();
}
