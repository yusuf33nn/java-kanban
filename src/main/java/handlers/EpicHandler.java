package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Epic;
import models.Subtask;

import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private static final String basePath = "epics";

    public EpicHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        EpicEndpoint epicEndpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (epicEndpoint) {
            case GET_EPIC -> getById(exchange, Epic.class);
            case GET_ALL_EPICS -> getAll(exchange, Epic.class);
            case GET_EPIC_SUBTASKS -> getEpicSubtasks(exchange);
            case POST_EPIC -> create(exchange, Epic.class);
            case UPDATE_EPIC -> update(exchange, Epic.class);
            case DELETE_EPIC -> deleteById(exchange);
            case UNKNOWN -> sendError(exchange, new RuntimeException("No such method"));
        }
    }

    private void getEpicSubtasks(HttpExchange exchange) {
        long epicId = retrieveIdFromPath(exchange);
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epicId);
        try {
            var resp = gson.toJson(subtasks);
            sendOk(exchange, resp);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    private EpicEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts[1].equals(basePath)) {
            EpicEndpoint result = null;
            switch (pathParts.length) {
                case 2 -> {
                    if (requestMethod.equals(GET_METHOD)) {
                        result = EpicEndpoint.GET_ALL_EPICS;
                    } else {
                        result = EpicEndpoint.POST_EPIC;
                    }
                }
                case 3 -> result = switch (requestMethod) {
                    case GET_METHOD -> EpicEndpoint.GET_EPIC;
                    case POST_METHOD -> EpicEndpoint.UPDATE_EPIC;
                    case DELETE_METHOD -> EpicEndpoint.DELETE_EPIC;
                    default -> throw new IllegalStateException("Unexpected value: " + requestMethod);
                };
                case 4 -> {
                    if ("subtasks".equals(pathParts[3])) {
                        result = EpicEndpoint.GET_EPIC_SUBTASKS;
                    } else {
                        result = EpicEndpoint.UNKNOWN;
                    }
                }
            }
            return result;
        } else {
            return EpicEndpoint.UNKNOWN;
        }
    }

    enum EpicEndpoint {GET_EPIC, GET_ALL_EPICS, GET_EPIC_SUBTASKS, POST_EPIC, UPDATE_EPIC, DELETE_EPIC, UNKNOWN}
}
