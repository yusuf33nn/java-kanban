import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handlers.EpicHandler;
import handlers.HistoryHandler;
import handlers.PrioritizedHandler;
import handlers.SubtaskHandler;
import handlers.TaskHandler;
import manager.TaskManager;
import utils.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getInMemoryTaskManager();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
