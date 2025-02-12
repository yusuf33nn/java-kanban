import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import typetoken.SubtaskListTypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static manager.InMemoryTaskManager.getTaskIdCounter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static utils.HttpStatusCodeConstants.CREATED;
import static utils.HttpStatusCodeConstants.NOT_ACCEPTABLE;
import static utils.HttpStatusCodeConstants.NOT_FOUND;
import static utils.HttpStatusCodeConstants.OK;

public class HttpTaskServerSubtasksTest {
    private static final String BASE_URI = "http://localhost:8080/subtasks";
    private final Epic epic = new Epic("epic", "epic_desc", LocalDateTime.now(), Duration.ZERO);
    TaskManager manager = new InMemoryTaskManager();
    Gson gson =
            new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
    HttpTaskServer taskServer = new HttpTaskServer(manager, gson);

    public HttpTaskServerSubtasksTest() throws IOException {}

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.resetCounter();
        taskServer.startServer();
        manager.createNewTask(epic);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void shouldAddSubtask_and_return_201() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask.setEpic(epic);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CREATED, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("subtask1", subtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void shouldAddSubtask_and_return_406() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask1.setEpic(epic);
        String subtaskJson1 = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson1)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CREATED, response.statusCode());

        Subtask subtask2 = new Subtask("subtask2", "subtask2_desc2", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask2.setEpic(epic);
        String subtaskJson2 = gson.toJson(subtask1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(NOT_ACCEPTABLE, response2.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    public void should_returnSubtaskList_and_200_code() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask1.setEpic(epic);
        String subtaskJson1 = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson1)).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask2 = new Subtask("subtask2", "subtask2_desc2", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask2.setEpic(epic);
        String subtaskJson2 = gson.toJson(subtask1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response.statusCode());

        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(subtasksFromManager, subtasksFromResponse);
    }

    @Test
    public void should_findSubtaskById_and_return_200_code() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask1.setEpic(epic);
        String subtaskJson1 = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson1)).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create(BASE_URI + "/" + getTaskIdCounter());
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response.statusCode());

        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);
        Subtask subtaskFromManager = manager.getSubtask(getTaskIdCounter());

        assertNotNull(subtaskFromManager, "Подзадача не найдена");
        assertEquals(subtaskFromManager, subtaskFromResponse);
    }

    @Test
    public void should_not_findSubtaskById_and_return_404_code() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask1.setEpic(epic);
        String subtaskJson1 = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson1)).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create(BASE_URI + "/" + (getTaskIdCounter() + 1));
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(NOT_FOUND, response.statusCode());

        Subtask taskFromManager = manager.getSubtask((getTaskIdCounter() + 1));

        assertNull(taskFromManager, "Подзадача найдена");
    }

    @Test
    public void should_deleteSubtaskById_and_return_200_code() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask1.setEpic(epic);
        manager.createNewTask(subtask1);
        long subtaskId = 2L;
        Subtask subtaskFromManager = manager.getSubtask(subtaskId);
        assertNotNull(subtaskFromManager);

        HttpClient client = HttpClient.newHttpClient();

        URI url2 = URI.create(BASE_URI + "/" + subtaskId);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response.statusCode());

        Subtask subtaskFromManagerAfterDeletion = manager.getSubtask(subtaskId);

        assertNull(subtaskFromManagerAfterDeletion, "Подзадача не найдена");
    }

    @Test
    public void should_not_deleteSubtaskById_and_return_404_code() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(200));
        subtask1.setEpic(epic);
        manager.createNewTask(subtask1);
        long subtaskId = 3L;
        Subtask subtaskFromManager = manager.getSubtask(subtaskId);
        assertNull(subtaskFromManager);

        HttpClient client = HttpClient.newHttpClient();

        URI url2 = URI.create(BASE_URI + "/" + subtaskId);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(NOT_FOUND, response.statusCode());
    }
}
