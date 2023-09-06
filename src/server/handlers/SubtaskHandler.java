package server.handlers;

import server.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class SubtaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();


    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;
        String method = exchange.getRequestMethod();
        URI requestURI = exchange.getRequestURI();
        String path = String.valueOf(requestURI);

        switch (method) {
            case "GET":
                String query = requestURI.getQuery();
                if (query == null) {
                    statusCode = 200;
                    response = gson.toJson(taskManager.getAllSubtasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask != null) {
                            response = gson.toJson(subtask);
                            statusCode = 200;
                        } else {
                            response = "Подзадача с данным id не найдена";
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
                        Subtask subtask = gson.fromJson(bodyRequest, Subtask.class);
                        int id = subtask.getId();
                        if (taskManager.getSubtaskById(id) != null) {
                            taskManager.updateTask(subtask);
                            statusCode = 200;
                            response = "Подзадача с id=" + id + " обновлена";
                        } else {
                            System.out.println("ADDED");
                            Subtask subtaskAdded = taskManager.addSubtask(subtask);
                            System.out.println("ADDED SUBTASK: " + subtaskAdded);
                            int idAdded = subtaskAdded.getId();
                            statusCode = 201;
                            response = "Добавлена подзадача с id=" + idAdded;
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        statusCode = 400;
                    }
                }
                break;
            case "DELETE":
                response = "";
                query = requestURI.getQuery();
                if (query == null) {
                    taskManager.deleteAllSubtasks();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteSubtaskById(id);
                        statusCode = 200;
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

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
         */
        exchange.sendResponseHeaders(statusCode, 0);
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
    }
}