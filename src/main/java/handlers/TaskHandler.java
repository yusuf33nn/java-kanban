package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Task;

public class TaskHandler extends BaseHttpHandler {
    private static final String basePath = "tasks";

    public TaskHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASK -> getById(exchange, Task.class);
            case GET_ALL_TASKS -> getAll(exchange, Task.class);
            case POST_TASK -> create(exchange, Task.class);
            case UPDATE_TASK -> update(exchange, Task.class);
            case DELETE_TASK -> deleteById(exchange);
            case UNKNOWN -> sendError(exchange, new RuntimeException("No such method"));
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals(basePath)) {
            if (requestMethod.equals(GET_METHOD)) {
                return Endpoint.GET_ALL_TASKS;
            } else {
                return Endpoint.POST_TASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals(basePath)) {
            return switch (requestMethod) {
                case GET_METHOD -> Endpoint.GET_TASK;
                case POST_METHOD -> Endpoint.UPDATE_TASK;
                case DELETE_METHOD -> Endpoint.DELETE_TASK;
                default -> throw new IllegalStateException("Unexpected value: " + requestMethod);
            };
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint { GET_TASK, GET_ALL_TASKS, POST_TASK, UPDATE_TASK, DELETE_TASK, UNKNOWN }
}
