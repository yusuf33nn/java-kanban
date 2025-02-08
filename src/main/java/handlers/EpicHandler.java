package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        Gson gson = new Gson();

    }
}
