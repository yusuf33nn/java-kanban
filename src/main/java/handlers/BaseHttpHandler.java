package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.NotFoundException;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class BaseHttpHandler implements HttpHandler {
    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int NOT_FOUND = 404;
    private static final int NOT_ACCEPTABLE = 406;
    private static final int INTERNAL_SERVER_ERROR = 500;

    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String DELETE_METHOD = "DELETE";

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

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
        String id = exchange.getRequestURI().getPath().split("/")[2];
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected void getAll(HttpExchange exchange, Class<? extends Task> tClass) {
        try {
            List<? extends Task> tasks;
            if (tClass.equals(Task.class)) {
                tasks = taskManager.getAllTasks();
            } else if (tClass.equals(Subtask.class)) {
                tasks = taskManager.getAllSubtasks();
            } else if (tClass.equals(Epic.class)) {
                tasks = taskManager.getAllEpics();
            } else {
                throw new IllegalArgumentException("Неизвестный тип задачи: " + tClass);
            }
            var resp = gson.toJson(tasks);
            sendOk(exchange, resp);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    protected void getById(HttpExchange exchange, Class<? extends Task> tClass) {
        try {
            long id = retrieveIdFromPath(exchange);
            Task task;
            if (tClass.equals(Task.class)) {
                task = taskManager.getTask(id);
            } else if (tClass.equals(Subtask.class)) {
                task = taskManager.getSubtask(id);
            } else if (tClass.equals(Epic.class)) {
                task = taskManager.getEpic(id);
            } else {
                throw new IllegalArgumentException("Неизвестный тип задачи: " + tClass);
            }
            var resp = gson.toJson(task);
            sendOk(exchange, resp);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    protected void deleteById(HttpExchange exchange) {
        try {
            long taskId = retrieveIdFromPath(exchange);
            taskManager.removeById(taskId);
            sendOk(exchange, "");
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    protected void create(HttpExchange exchange, Class<? extends Task> tClass) {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(requestBody, tClass);
            taskManager.createNewTask(task);
            sendCreated(exchange);
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    protected void update(HttpExchange exchange, Class<? extends Task> tClass) {
        try {
            long taskId = retrieveIdFromPath(exchange);
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(requestBody, tClass);
            task.setId(taskId);
            taskManager.updateTask(task);
            sendCreated(exchange);
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }
}
