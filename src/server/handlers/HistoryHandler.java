package server.handlers;

import server.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class HistoryHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode = 400;
        String response;
        String method = exchange.getRequestMethod();
        URI requestURI = exchange.getRequestURI();
        String path = String.valueOf(requestURI);

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                statusCode = 200;
                response = gson.toJson(taskManager.getHistory());
                break;
            default:
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
