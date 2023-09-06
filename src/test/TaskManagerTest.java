package test;

import manager.ManagerValidateException;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected Task createTask() {
        return new Task("Title", "Description", Instant.now(), 0);
    }

    protected Epic createEpic() {

        return new Epic("Title", "Description", Instant.now(), 0);
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask(epic.getId(), "Title", "Description", Instant.now(), 0);
    }

    @Test
    public void testCreateTask() {
        Task task = createTask();
        manager.addTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void testCreateEpic() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubtaskIds());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void testCreateSubtask() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.addSubtask(subtask);
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubtaskIds());
    }

    @Test
    void testReturnNullWhenCreateTaskNull() {
        assertNull(manager.getTaskById(8888));
    }

    @Test
    void testReturnNullWhenCreateEpicNull() {
        assertNull(manager.getEpicById(8888));
    }

    @Test
    void testReturnNullWhenCreateSubtaskNull() {
        assertNull(manager.getSubtaskById(8888));
    }

    @Test
    public void testUpdateTaskStatusToInProgress() {
        Task task = createTask();
        manager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void testUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void testUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void testUpdateTaskStatusToInDone() {
        Task task = createTask();
        manager.addTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void testUpdateSubtaskStatusToInDone() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.addSubtask(subtask);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, manager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void testUpdateEpicStatusToInDone() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void testNotUpdateTaskIfNull() {
        Task task = createTask();
        manager.addTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void testNotUpdateSubtaskIfNull() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.addSubtask(subtask);
        manager.updateSubtask(null);
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void testNotUpdateEpicIfNull() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        manager.updateEpic(null);
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    public void testDeleteAllTasks() {
        Task task = createTask();
        manager.addTask(task);
        manager.deleteAllTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
    }

    @Test
    public void testDeleteAllSubtasks() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.addSubtask(subtask);
        manager.deleteAllSubtasks();
        assertTrue(epic.getSubtaskIds().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void testDeleteAllEpics() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        manager.deleteAllEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
    }

    @Test
    public void testDeleteAllSubtasksByEpic() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.addSubtask(subtask);
        manager.deleteAllSubtasksByEpic(epic);
        assertTrue(epic.getSubtaskIds().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void testDeleteTaskById() {
        Task task = createTask();
        manager.addTask(task);
        manager.deleteTaskById(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
    }

    @Test
    public void testDeleteEpicById() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        manager.deleteEpicById(epic.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
    }

    @Test
    public void testNotDeleteTaskIfWrongId() {
        Task task = createTask();
        manager.addTask(task);
        manager.deleteTaskById(8888);
        assertEquals(List.of(task), manager.getAllTasks());
    }

    @Test
    public void testNotDeleteSubtaskIfWrongId() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.addSubtask(subtask);
        manager.deleteSubtaskById(8888);
        assertEquals(List.of(subtask), manager.getAllSubtasks());
        assertEquals(List.of(subtask.getId()), manager.getEpicById(epic.getId()).getSubtaskIds());
    }

    @Test
    public void testNotDeleteEpicIfWrongId() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        manager.deleteEpicById(8888);
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    public void testDoNothingIfTaskHashMapIsEmpty() {
        manager.deleteAllTasks();
        manager.deleteTaskById(8888);
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void testDoNothingIfSubtaskHashMapIsEmpty() {
        manager.deleteAllEpics();
        manager.deleteSubtaskById(8888);
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void testDoNothingIfEpicHashMapIsEmpty() {
        manager.deleteAllEpics();
        manager.deleteEpicById(8888);
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void testReturnEmptyListWhenGetSubtaskByEpicIdIsEmpty() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        List<Subtask> subtasks = manager.getAllSubtasksByEpicId(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void testReturnEmptyListTasksIfNoTasks() {
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void testReturnEmptyListSubtasksIfNoSubtasks() {
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void testReturnEmptyListEpicsIfNoEpics() {
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void testReturnNullIfTaskDoesNotExist() {
        assertNull(manager.getTaskById(8888));
    }

    @Test
    public void testReturnNullIfSubtaskDoesNotExist() {
        assertNull(manager.getSubtaskById(8888));
    }

    @Test
    public void testReturnNullIfEpicDoesNotExist() {
        assertNull(manager.getEpicById(8888));
    }

    @Test
    public void testReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void testReturnEmptyHistoryIfTasksNotExist() {
        manager.getTaskById(8888);
        manager.getSubtaskById(8888);
        manager.getEpicById(8888);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void testReturnHistoryWithTasks() {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.addSubtask(subtask);
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        List<Task> list = manager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }

    @Test
    void testOfValidation() {
        ArrayList<Task> list = new ArrayList<>(manager.getPrioritizedTasksSet());
        assertEquals(0, list.size());

        Instant startTime1 = Instant.parse("2022-09-01T10:00:00Z");
        long longDuration1 = 90;
        Task task1 = new Task("Task 1", "Description 1", startTime1, longDuration1);

        manager.addTask(task1);
        list = new ArrayList<> (manager.getPrioritizedTasksSet());
        assertEquals(1, list.size());
    }

    @Test
    public void testGetPrioritizedTasksTest() {
        Instant startTime1 = Instant.parse("2022-09-01T10:00:00Z");  // тут 2022 год
        long longDuration1 = 90;
        Instant startTime2 = Instant.parse("2023-09-01T10:00:00Z");  // тут 2023 год
        long longDuration2 = 90;
        Task task1 = new Task("Task 1", "Description 1", startTime1, longDuration1);
        Task task2 = new Task("Task 2", "Description 2", startTime2, longDuration2);
        manager.addTask(task1);
        manager.addTask(task2);
        ArrayList<Task> list = new ArrayList<> (manager.getPrioritizedTasksSet());
        assertEquals(2, list.size()); // обе задачи добавляются, поэтому 2

    }

    @Test
    public void testAssertThrowsWithTheSameTime() {
        Instant startTime1 = Instant.parse("2023-09-01T10:00:00Z");  // одинаковые
        long longDuration1 = 90;
        Instant startTime2 = Instant.parse("2023-09-01T10:00:00Z");  // одинаковые
        long longDuration2 = 90;
        Task task1 = new Task("Task 1", "Description 1", startTime1, longDuration1);
        Task task2 = new Task("Task 2", "Description 2", startTime2, longDuration2);
        manager.addTask(task1);
        Assertions.assertThrows(ManagerValidateException.class, () -> {
            manager.addTask(task2);
        });
    }

    @Test
    public void testAssertThrowsWithIntersection() {

        Instant startTime1 = Instant.parse("2023-07-27T08:00:00Z");
        long longDuration1 = 10800;

        Task existingTask = new Task("Task 1", "Description 1", startTime1, longDuration1);
        manager.addTask(existingTask);

        Instant startTime2 = Instant.parse("2023-07-27T09:30:00Z");
        long longDuration2 = 10800;

        Task newTask = new Task ("Task 2", "Description 2", startTime2, longDuration2);

        Assertions.assertThrows(ManagerValidateException.class, () -> {
            manager.addTask(newTask);
        });
    }
}

