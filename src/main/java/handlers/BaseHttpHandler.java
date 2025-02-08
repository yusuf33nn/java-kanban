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
    private static final int NOT_ACCEPTABLE = 405;
    private static final int INTERNAL_SERVER_ERROR = 500;

    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendOk(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(OK, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendCreated(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, CREATED);
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, NOT_FOUND);
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        sendResponse(httpExchange, NOT_ACCEPTABLE);
    }

    protected void sendError(HttpExchange httpExchange, Exception e) throws IOException {
        httpExchange.sendResponseHeaders(INTERNAL_SERVER_ERROR, 0);
        httpExchange.getResponseBody().write(e.getMessage().getBytes());
        httpExchange.close();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }

    private void sendResponse(HttpExchange exchange, int httpCode) throws IOException {
        exchange.sendResponseHeaders(httpCode, 0);
        exchange.close();
    }
}
