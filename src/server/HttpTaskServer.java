package server;


import com.sun.net.httpserver.HttpServer;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;
    KVServer kvServer = new KVServer();
    private HistoryManager historyManager;

    private TaskManager taskManager;

    public HttpTaskServer() throws IOException, InterruptedException {
        startServer();
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);

        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/epic/", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/subtask/epic/", new SubtaskByEpicHandler(taskManager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(taskManager));
        httpServer.createContext("/tasks/", new TasksHandler(taskManager));
    }

    private void startServer() throws IOException {
        kvServer.start();
    }

    private void stopServer() throws IOException {
        kvServer.stop();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() throws IOException {
        httpServer.stop(1);
        stopServer();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new HttpTaskServer().start();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

}
