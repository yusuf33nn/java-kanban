package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {


    public PrioritizedHandler(Gson gson, TaskManager taskManager) {
        super(gson, taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
    }
}
