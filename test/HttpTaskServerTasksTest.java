import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Task;
import models.TaskType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import typetoken.TaskListTypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static utils.HttpStatusCodeConstants.CREATED;
import static utils.HttpStatusCodeConstants.NOT_ACCEPTABLE;
import static utils.HttpStatusCodeConstants.NOT_FOUND;
import static utils.HttpStatusCodeConstants.OK;

public class HttpTaskServerTasksTest {

    private static final String BASE_URI = "http://localhost:8080/tasks";
    TaskManager manager = new InMemoryTaskManager();

    Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
    HttpTaskServer taskServer = new HttpTaskServer(manager, gson);

    public HttpTaskServerTasksTest() throws IOException {}

    @BeforeEach
    public void setUp() {
        manager.removeTasks();
        manager.resetCounter();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void shouldAddTask_and_return_201() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CREATED, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldAddTask_and_return_406() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(CREATED, response1.statusCode());

        Task task2 = new Task("Test 2", "Testing task 2", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson2 = gson.toJson(task2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(NOT_ACCEPTABLE, response2.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void should_returnTaskList_and_200_code() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Test 2", "Testing task 2", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson2 = gson.toJson(task2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(tasksFromManager, tasksFromResponse);
    }

    @Test
    public void should_findTaskById_and_return_200_code() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create(BASE_URI + "/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response.statusCode());

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        Task taskFromManager = manager.getTask(1L);

        assertNotNull(taskFromManager, "Задача не найдена");
        assertEquals(taskFromManager, taskFromResponse);
    }

    @Test
    public void should_not_findTaskById_and_return_404_code() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create(BASE_URI + "/2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(NOT_FOUND, response.statusCode());

        Task taskFromManager = manager.getTask(2L);

        assertNull(taskFromManager, "Задача найдена");
    }

    @Test
    public void should_deleteTaskById_and_return_200_code() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create(BASE_URI + "/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response.statusCode());

        Task taskFromManager = manager.getTask(1L);

        assertNull(taskFromManager, "Задача найдена");
    }

    @Test
    public void should_not_deleteTaskById_and_return_404_code() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", TaskType.TASK,
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson1 = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create(BASE_URI + "/2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(NOT_FOUND, response.statusCode());

        Task taskFromManager = manager.getTask(2L);

        assertNull(taskFromManager, "Задача найдена");
    }
}
