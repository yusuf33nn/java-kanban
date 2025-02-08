package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Subtask;

public class SubtaskHandler extends BaseHttpHandler {

    private static final String basePath = "subtasks";


    public SubtaskHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        SubtaskEndpoint subtaskEndpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (subtaskEndpoint) {
            case GET_SUBTASK -> getById(exchange, Subtask.class);
            case GET_ALL_SUBTASKS -> getAll(exchange, Subtask.class);
            case POST_SUBTASK, UPDATE_SUBTASK-> createOrUpdateTask(exchange, Subtask.class);
            case DELETE_SUBTASK -> deleteById(exchange);
            case UNKNOWN -> sendError(exchange, new RuntimeException("No such method"));
        }
    }

    private SubtaskEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals(basePath)) {
            if (requestMethod.equals(GET_METHOD)) {
                return SubtaskEndpoint.GET_ALL_SUBTASKS;
            } else {
                return SubtaskEndpoint.POST_SUBTASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals(basePath)) {
            return switch (requestMethod) {
                case GET_METHOD -> SubtaskEndpoint.GET_SUBTASK;
                case POST_METHOD -> SubtaskEndpoint.UPDATE_SUBTASK;
                case DELETE_METHOD -> SubtaskEndpoint.DELETE_SUBTASK;
                default -> throw new IllegalStateException("Unexpected value: " + requestMethod);
            };
        }
        return SubtaskEndpoint.UNKNOWN;
    }

    enum SubtaskEndpoint {GET_SUBTASK, GET_ALL_SUBTASKS, POST_SUBTASK, UPDATE_SUBTASK, DELETE_SUBTASK, UNKNOWN}
}
