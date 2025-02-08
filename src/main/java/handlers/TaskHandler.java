package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Task;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private static final String basePath = "tasks";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASK -> throw new RuntimeException() ;
            case GET_ALL_TASKS -> {
                List<Task> tasks = taskManager.getAllTasks();
                var resp = gson.toJson(tasks);
                try {
                    sendOk(exchange, resp);
                } catch (IOException e) {
                    sendError(exchange, e);
                }
            }
            case POST_TASK -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                var task = gson.fromJson(requestBody, Task.class);
                taskManager.createNewTask(task);
                try {
                    sendCreated(exchange);
                } catch (IOException e) {
                    sendError(exchange, e);
                }
            }
            case UPDATE_TASK -> throw new RuntimeException();
            case DELETE_TASK ->throw new RuntimeException() ;
            case UNKNOWN -> throw new RuntimeException();
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals(basePath)) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL_TASKS;
            } else {
                return Endpoint.POST_TASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals(basePath)) {
            return switch (requestMethod) {
                case "GET" -> Endpoint.GET_TASK;
                case "POST" -> Endpoint.UPDATE_TASK;
                case "DELETE" -> Endpoint.DELETE_TASK;
                default -> throw new IllegalStateException("Unexpected value: " + requestMethod);
            };
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint {GET_TASK, GET_ALL_TASKS, POST_TASK, UPDATE_TASK, DELETE_TASK, UNKNOWN}
}
