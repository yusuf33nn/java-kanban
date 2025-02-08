package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Epic;

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
            case POST_EPIC, UPDATE_EPIC -> createOrUpdateTask(exchange, Epic.class);
            case DELETE_EPIC -> deleteById(exchange);
            case UNKNOWN -> sendError(exchange, new RuntimeException("No such method"));
        }

    }

    private EpicEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals(basePath)) {
            if (requestMethod.equals(GET_METHOD)) {
                return EpicEndpoint.GET_ALL_EPICS;
            } else {
                return EpicEndpoint.POST_EPIC;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals(basePath)) {
            return switch (requestMethod) {
                case GET_METHOD -> EpicEndpoint.GET_EPIC;
                case POST_METHOD -> EpicEndpoint.UPDATE_EPIC;
                case DELETE_METHOD -> EpicEndpoint.DELETE_EPIC;
                default -> throw new IllegalStateException("Unexpected value: " + requestMethod);
            };
        }
        return EpicEndpoint.UNKNOWN;
    }

    enum EpicEndpoint {GET_EPIC, GET_ALL_EPICS, POST_EPIC, UPDATE_EPIC, DELETE_EPIC, UNKNOWN}
}
