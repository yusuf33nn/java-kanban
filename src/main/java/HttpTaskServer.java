import adapters.DurationAdapter;
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
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {


        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        TaskManager taskManager = Managers.getInMemoryTaskManager();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(gson, taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(gson, taskManager));
        httpServer.createContext("/epics", new EpicHandler(gson, taskManager));
        httpServer.createContext("/history", new HistoryHandler(gson, taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(gson, taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
