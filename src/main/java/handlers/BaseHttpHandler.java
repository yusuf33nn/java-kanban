package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {
    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int NOT_FOUND = 404;
    private static final int NOT_ACCEPTABLE = 406;
    private static final int INTERNAL_SERVER_ERROR = 500;

    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String DELETE_METHOD = "DELETE";

    protected final Gson gson;
    protected final TaskManager taskManager;

    public BaseHttpHandler(Gson gson, TaskManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }


    protected void sendOk(HttpExchange h, String text) {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try {
            h.sendResponseHeaders(OK, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        h.close();
    }

    protected void sendCreated(HttpExchange httpExchange) {
        sendResponse(httpExchange, CREATED);
    }

    protected void sendNotFound(HttpExchange httpExchange) {
        sendResponse(httpExchange, NOT_FOUND);
    }

    protected void sendHasInteractions(HttpExchange httpExchange) {
        sendResponse(httpExchange, NOT_ACCEPTABLE);
    }

    protected void sendError(HttpExchange httpExchange, Exception e) {
        try {
            httpExchange.sendResponseHeaders(INTERNAL_SERVER_ERROR, 0);
            httpExchange.getResponseBody().write(e.getMessage().getBytes());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        httpExchange.close();
    }

    private void sendResponse(HttpExchange exchange, int httpCode) {
        try {
            exchange.sendResponseHeaders(httpCode, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        exchange.close();
    }

    protected long retrieveIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String id = path.substring(path.lastIndexOf("/") + 1);
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }
}
