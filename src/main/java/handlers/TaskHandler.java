package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Task;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private static final String basePath = "tasks";

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASK -> se ;
            case GET_ALL_TASKS -> ;
            case POST_TASK -> {
                this.taskManager.createNewTask(exchange.getRequestBody())
            }
            case UPDATE_TASK -> ;
            case DELETE_TASK -> ;
            case UNKNOWN -> ;
        }

        Gson gson = new Gson();
        List<Task> tasks = this.taskManager.getAllTasks();
        var resp = gson.toJson(tasks);
        try {
            sendText(exchange, resp);
        } catch (IOException e) {
            sendError(exchange, e);
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
