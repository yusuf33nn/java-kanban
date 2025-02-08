package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ManagerSaveException;
import manager.TaskManager;
import models.Task;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private static final String basePath = "tasks";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public TaskHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }


    @Override
    public void handle(HttpExchange exchange) {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASK -> getTaskById(exchange);
            case GET_ALL_TASKS -> getAllTasks(exchange);
            case POST_TASK, UPDATE_TASK -> createOrUpdateTask(exchange);
            case DELETE_TASK -> deleteTaskById(exchange);
            case UNKNOWN -> sendError(exchange, new RuntimeException("No such method"));
        }
    }

    private void getAllTasks(HttpExchange exchange) {
        try {
            List<Task> tasks = taskManager.getAllTasks();
            var resp = gson.toJson(tasks);
            sendOk(exchange, resp);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private void getTaskById(HttpExchange exchange) {
        try {
            long taskId = retrieveIdFromPath(exchange);
            Task task = taskManager.getTask(taskId);
            var resp = gson.toJson(task);
            sendOk(exchange, resp);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private void deleteTaskById(HttpExchange exchange) {
        try {
            long taskId = retrieveIdFromPath(exchange);
            taskManager.removeById(taskId);
            sendOk(exchange, "");
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private void createOrUpdateTask(HttpExchange exchange) {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(requestBody, Task.class);
            taskManager.createNewTask(task);
            sendCreated(exchange);
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendError(exchange, e);
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

    enum Endpoint {GET_TASK, GET_ALL_TASKS, POST_TASK, UPDATE_TASK, DELETE_TASK, UNKNOWN}
}
