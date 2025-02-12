import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import typetoken.EpicListTypeToken;
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
import static utils.HttpStatusCodeConstants.NOT_FOUND;
import static utils.HttpStatusCodeConstants.OK;

public class HttpTaskServerEpicsTest {
    private static final String BASE_URI = "http://localhost:8080/epics";

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerEpicsTest() throws IOException {}

    @BeforeEach
    public void setUp() {
        manager.removeEpics();
        manager.resetCounter();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void shouldAddEpic_and_return_201() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic_desc", LocalDateTime.now(), Duration.ZERO);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CREATED, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("epic", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
    }



    @Test
    public void should_returnEpicList_and_200_code() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic_desc", LocalDateTime.now(), Duration.ZERO);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic2 = new Epic("epic2", "epic_desc2", LocalDateTime.now(), Duration.ZERO);
        String epicJson2 = gson.toJson(epic2);

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response.statusCode());

        List<Epic> epicsFromResponse = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(epicsFromManager, epicsFromResponse);
    }

    @Test
    public void should_findEpicById_and_return_200_code() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic_desc", LocalDateTime.now(), Duration.ZERO);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(CREATED, response.statusCode());


        URI url2 = URI.create(BASE_URI + "/" + getTaskIdCounter());
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response2.statusCode());

        Epic epicFromResponse = gson.fromJson(response2.body(), Epic.class);
        Epic epicFromManager = manager.getEpic(getTaskIdCounter());

        assertNotNull(epicFromManager, "Эпик не найден");
        assertEquals(epicFromManager, epicFromResponse);
    }

    @Test
    public void should_not_findEpicById_and_return_404_code() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic_desc", LocalDateTime.now(), Duration.ZERO);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create(BASE_URI + "/" + (getTaskIdCounter() + 1));
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(NOT_FOUND, response.statusCode());

        Subtask taskFromManager = manager.getSubtask((getTaskIdCounter() + 1));

        assertNull(taskFromManager, "Эпик найден");
    }

    @Test
    public void should_findSubtasksByEpicId_and_return_200_code() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic_desc", LocalDateTime.now(), Duration.ZERO);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URI);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromManager = manager.getEpic(getTaskIdCounter());

        Subtask subtask1 = new Subtask("subtask1", "subtask1_desc1", LocalDateTime.now(), Duration.ofMinutes(100));
        subtask1.setEpic(epicFromManager);
        manager.createNewTask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "subtask2_desc2", LocalDateTime.now().plusDays(1), Duration.ofMinutes(200));
        subtask2.setEpic(epicFromManager);
        manager.createNewTask(subtask2);


        URI url2 = URI.create(BASE_URI + "/" + epicFromManager.getId() + "/subtasks");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(OK, response.statusCode());

        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        List<Subtask> subtasksFromManager = manager.getEpicSubtasks(epicFromManager.getId());

        assertNotNull(subtasksFromManager, "Сабтаски не найдены");
        assertEquals(subtasksFromManager, subtasksFromResponse);
    }
}
