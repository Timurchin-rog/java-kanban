package service.handlersTest;

import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.InMemoryHistoryManager;
import service.http.HttpTaskServer;
import service.memory.InMemoryTaskManager;
import service.memory.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Обработчик подзадач HTTP-сервера")
public class SubtaskHandlerTest {

    HttpTaskServer httpTaskServer;
    Gson gson;
    TaskManager taskManager;
    HttpClient client;
    Subtask subtask;
    Epic epic;
    String subtaskJson;

    @BeforeEach
    void start() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = httpTaskServer.getGson();
        httpTaskServer.startServer();
        client = HttpClient.newHttpClient();
        epic = taskManager.createEpic(new Epic(Type.EPIC, "Test Epic", "Test Epic Description"));
        subtask = taskManager.createSubtask(new Subtask(Type.SUBTASK, "Test Subtask", "NEW",
                "Test Subtask description", Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 0, 0), epic.getId()));
        subtaskJson = gson.toJson(subtask);
    }

    @AfterEach
    void stop() {
        httpTaskServer.stopServer();
    }

    @Test
    @DisplayName("Должен отправлять клиенту все подзадачи")
    void shouldSendToClientAllSubtasks() {
        URI url = URI.create(String.format("http://localhost:%d/subtasks", httpTaskServer.getPORT()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус при получении подзадач");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска подзадач возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен отправлять клиенту подзадачу по ID")
    void shouldSendToClientSubtaskByID() {
        URI url = URI.create(String.format("http://localhost:%d/subtasks/%d", httpTaskServer.getPORT(), subtask.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус при получении подзадачи");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска подзадачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен выбрасывать NotFoundException")
    void shouldThrowNotFoundException() {
        URI url = URI.create(String.format("http://localhost:%d/subtasks/%d", httpTaskServer.getPORT(), subtask.getId() + 1));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Неверный код при поиске несуществующей подзадачи");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска подзадачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен добавлять подзадачу")
    void shouldAddSubtask() {
        URI url = URI.create(String.format("http://localhost:%d/subtasks", httpTaskServer.getPORT()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при добавлении подзадачи");
            HashMap<Integer, Subtask> subtasksFromManager = taskManager.getAllSubtasks();

            assertNotNull(subtasksFromManager, "Подзадачи не возвращаются при добавлении");
            assertEquals(2, subtasksFromManager.size(), "Некорректное количество подзадач при добавлении");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время добавления подзадачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен выбрасывать ValidationException")
    void shouldThrowValidationException() {
        URI url = URI.create(String.format("http://localhost:%d/subtasks", httpTaskServer.getPORT()));
        Subtask subtaskAdded = new Subtask(Type.SUBTASK, "Test SubtaskAdd", "NEW",
                "Test SubtaskAdd description", Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 5, 0), epic.getId());
        String subtaskAddedJson = gson.toJson(subtaskAdded);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskAddedJson)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode(),
                    "Неверный код при пересечении подзадачи по времени с уже существующей");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время добавления подзадачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен обновлять подзадачу по ID")
    void shouldUpdateSubtaskByID() {
        URI url = URI.create(String.format("http://localhost:%d/subtasks/%d", httpTaskServer.getPORT(), subtask.getId()));
        Subtask subtaskUpd = new Subtask(Type.SUBTASK, "Test SubtaskUpd", "NEW",
                "Test SubtaskUpd description", Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 20, 0), epic.getId());
        String subtaskUpdJson = gson.toJson(subtaskUpd);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskUpdJson)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при обновлении подзадачи");

            HashMap<Integer, Subtask> subtasksFromManager = taskManager.getAllSubtasks();

            assertNotNull(subtasksFromManager, "Подзадачи не возвращаются при обновлении");
            assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач при обновлении");
            assertEquals("Test SubtaskUpd", subtasksFromManager.get(subtask.getId()).getName(),
                    "Не обновилось имя подзадачи");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время обновления подзадачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен удалять подзадачу по ID")
    void shouldDeleteSubtasksByID() {
        URI url = URI.create(String.format("http://localhost:%d/subtasks/%d", httpTaskServer.getPORT(), subtask.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при удалении подзадачи");

            HashMap<Integer, Subtask> subtasksEmptyMap = new HashMap<>();

            assertEquals(taskManager.getAllSubtasks(), subtasksEmptyMap, "Подзадача не удалена");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска подзадачи возникла ошибка");
        }
    }
}

