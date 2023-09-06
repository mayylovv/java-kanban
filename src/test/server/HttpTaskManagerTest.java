package test.server;

import server.HttpTaskManager;
import server.HttpTaskServer;
import server.KVServer;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import test.TaskManagerTest;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
//    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;

    @BeforeEach
    public void createManager() {
        try {
//            kvServer = new KVServer();
//            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
            manager = (HttpTaskManager) httpTaskServer.getTaskManager();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() throws IOException {
//        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void testLoadTasks() {
        Instant startTime1 = Instant.parse("2022-09-01T10:00:00Z");  // тут 2022 год
        long longDuration1 = 90;
        Instant startTime2 = Instant.parse("2023-09-01T10:00:00Z");  // тут 2023 год
        long longDuration2 = 90;
        Task task1 = new Task("Task 1", "Description 1", startTime1, longDuration1);
        Task task2 = new Task("Task 2", "Description 2", startTime2, longDuration2);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllTasks(), list);
    }

    @Test
    public void testLoad() {
        Instant startTime1 = Instant.parse("2022-09-01T10:00:00Z");  // тут 2022 год
        long longDuration1 = 90;

        Task task1 = new Task("Task 1", "Description 1", startTime1, longDuration1);

        manager.addTask(task1);

        manager.getTaskById(task1.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllTasks(), list);
    }


    @Test
    public void testLoadEpics() {
        Instant startTime1 = Instant.parse("2023-09-01T10:00:00Z");
        Instant startTime2 = Instant.parse("2023-09-01T10:00:00Z");


        Epic epic1 = new Epic("Epic 1", "Description 1", startTime1, 10000);
        Epic epic2 = new Epic("Epic 2", "Description 2", startTime2, 10);

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllEpics(), list);
    }



    @Test
    public void testLoadSubtasks() {
        Epic epic1 = new Epic("title1", "description1", Instant.now(), 10);
        Subtask subtask1 = new Subtask(epic1.getId(), "title1", "description1", Instant.now(), 6);
        Subtask subtask2 = new Subtask(epic1.getId(), "title2", "description2", Instant.now(), 7);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllSubtasks(), list);
    }

    @Test
    public void testGenerateId() throws IOException, InterruptedException {
        HttpTaskManager httpTaskManager = Managers.getDefault(httpTaskServer.getHistoryManager());
        Instant startTime1 = Instant.parse("2022-09-01T10:00:00Z");  // тут 2022 год
        long longDuration1 = 90;
        Instant startTime2 = Instant.parse("2023-09-01T10:00:00Z");  // тут 2023 год
        long longDuration2 = 90;
        Task task1 = new Task("Task 1", "Description 1", startTime1, longDuration1);
        Task task2 = new Task("Task 2", "Description 2", startTime2, longDuration2);
        manager.addTask(task1);
        manager.addTask(task2);
        httpTaskManager.load();
        List<Task> arrayList = httpTaskManager.getPrioritizedTasksSet();
        assertEquals(arrayList.get(0), task1);
        assertEquals(arrayList.get(1), task2);
    }

}