package test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager manager;
    private int id = 0;

    public int generateId() {
        return ++id;
    }

    protected Task createTask() {
        return new Task("Title", "Description");
    }

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void testHistoryIsEmpty() {
        Task task1 = createTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = createTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = createTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        manager.remove(task1.getId());
        manager.remove(task2.getId());
        manager.remove(task3.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void testAddTasksToHistory() {
        Task task1 = createTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = createTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = createTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        assertEquals(List.of(task1, task2, task3), manager.getHistory());
    }

    @Test
    public void testRemoveTask() {
        Task task1 = createTask();
        int newTaskId1 = generateId();
        task1.setId(newTaskId1);
        Task task2 = createTask();
        int newTaskId2 = generateId();
        task2.setId(newTaskId2);
        Task task3 = createTask();
        int newTaskId3 = generateId();
        task3.setId(newTaskId3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.remove(task2.getId());
        assertEquals(List.of(task1, task3), manager.getHistory());
    }

    @Test
    public void testRemoveOnlyOneTask() {
        Task task = createTask();
        int newTaskId = generateId();
        task.setId(newTaskId);
        manager.add(task);
        manager.remove(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void testNotRemoveTaskWithBadId() {
        Task task = createTask();
        int newTaskId = generateId();
        task.setId(newTaskId);
        manager.add(task);
        manager.remove(0);
        assertEquals(List.of(task), manager.getHistory());
    }
}