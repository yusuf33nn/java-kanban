package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            var tasks = taskManager.getHistoryManager().getHistory();
            var resp = gson.toJson(tasks);
            sendOk(exchange, resp);
        } catch (Exception e) {
            sendError(exchange, e);
        }
    }
}
