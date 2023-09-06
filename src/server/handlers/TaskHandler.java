package server.handlers;

import server.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class TaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;
        String method = exchange.getRequestMethod();
        URI requestURI = exchange.getRequestURI();
        String path = String.valueOf(requestURI);

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                String query = requestURI.getQuery();
                if (query == null) {
                    statusCode = 200;
                    String jsonString = gson.toJson(taskManager.getAllTasks());
                    System.out.println("GET TASKS: " + jsonString);
                    response = gson.toJson(jsonString);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Task task = taskManager.getTaskById(id);
                        if (task != null) {
                            response = gson.toJson(task);
                            statusCode = 200;
                        } else {
                            response = "Задача с данным id не найдена";
                            statusCode = 404;
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (bodyRequest.isEmpty()) {
                    statusCode = 400;
                    response = "Тело запроса пустое";
                } else {
                    try {
                        Task task = gson.fromJson(bodyRequest, Task.class);
                        int id = task.getId();
                        if (taskManager.getTaskById(id) != null) {
                            taskManager.updateTask(task);
                            statusCode = 201;
                            response = "Задача с id=" + id + " обновлена";
                        } else {
                            Task taskAdded = taskManager.addTask(task);
                            System.out.println("ADDED TASK: " + taskAdded);
                            int idAdded = taskAdded.getId();
                            statusCode = 201;
                            response = "Добавлена задача с id=" + idAdded;
                        }
                    } catch (JsonSyntaxException e) {
                        statusCode = 400;
                        response = "Неверный формат запроса";
                    }
                }
                break;
            case "DELETE":
                response = "";
                query = requestURI.getQuery();
                if (query == null) {
                    taskManager.deleteAllTasks();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteTaskById(id);
                        statusCode = 200;
                        response = "Задача с id=" + id + " была удалена";
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            default:
                statusCode = 400;
                response = "Некорректный запрос";
        }
        /*
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response.getBytes());
        }

         */

        exchange.sendResponseHeaders(statusCode, 0);
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);

    }
}