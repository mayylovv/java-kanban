package manager;

import tasks.Task;

import java.util.List;
import java.util.LinkedList;


public class InMemoryHistoryManager implements HistoryManager {
    int maxTasksInHistory = 10;
    List<Task> historyOfTasks = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (historyOfTasks.size() >= maxTasksInHistory) {
            historyOfTasks.remove(0);
        }
        historyOfTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyOfTasks;
    }
}
