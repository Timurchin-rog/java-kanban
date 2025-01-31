package service.handlersTest;

import com.google.gson.Gson;
import model.Task;
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

@DisplayName("Обработчик задач HTTP-сервера")
public class TaskHandlerTest {

    HttpTaskServer httpTaskServer;
    Gson gson;
    TaskManager taskManager;
    HttpClient client;
    Task task;
    String taskJson;

    @BeforeEach
    void start() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = httpTaskServer.getGson();
        httpTaskServer.startServer();
        client = HttpClient.newHttpClient();
        task = taskManager.createTask(new Task(Type.TASK, "Test Task", "NEW",
                "Test Task description", Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 0, 0)));
        taskJson = gson.toJson(task);
    }

    @AfterEach
    void stop() {
        httpTaskServer.stopServer();
    }

    @Test
    @DisplayName("Должен отправлять клиенту все задачи")
    void shouldSendToClientAllTasks() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус при получении задач");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска задач возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен отправлять клиенту задачу по ID")
    void shouldSendToClientTaskByID() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус при получении задачи");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска задачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен выбрасывать NotFoundException")
    void shouldThrowNotFoundException() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/tasks/" + task.getId() + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Неверный код при поиске несуществующей задачи");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска задачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен добавлять задачу")
    void shouldAddTask() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при добавлении задачи");
            HashMap<Integer, Task> tasksFromManager = taskManager.getAllTasks();

            assertNotNull(tasksFromManager, "Задачи не возвращаются при добавлении");
            assertEquals(2, tasksFromManager.size(), "Некорректное количество задач при добавлении");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время добавления задачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен выбрасывать ValidationException")
    void shouldThrowValidationException() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/tasks/");
        Task taskAdded = new Task(1, Type.TASK, "Test TaskAdded", "NEW", "Test Task description",
                Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 5, 0));
        String taskAddedJson = gson.toJson(taskAdded);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskAddedJson)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode(),
                    "Неверный код при пересечении задачи по времени с уже существующей");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время добавления задачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен обновлять задачу по ID")
    void shouldUpdateTaskByID() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/tasks/" + task.getId());
        Task taskUpd = new Task(1, Type.TASK, "Test TaskUpd", "NEW", "Test Task description",
                Duration.ofMinutes(10),
                LocalDateTime.of(2105, 1, 12, 12, 0, 0));
        String taskUpdJson = gson.toJson(taskUpd);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskUpdJson)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при обновлении задачи");

            HashMap<Integer, Task> tasksFromManager = taskManager.getAllTasks();

            assertNotNull(tasksFromManager, "Задачи не возвращаются при обновлении");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество задач при обновлении");
            assertEquals("Test TaskUpd", tasksFromManager.get(task.getId()).getName(), "Не обновилось имя задачи");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время обновления задачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен удалять все задачи")
    void shouldDeleteTasks() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при удалении всех задач");

            HashMap<Integer, Task> tasksEmptyMap = new HashMap<>();

            assertEquals(taskManager.getAllTasks(), tasksEmptyMap, "Задачи не удалены");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска задачи возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен удалять задачу по ID")
    void shouldDeleteTaskByID() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при удалении задачи");

            HashMap<Integer, Task> tasksEmptyMap = new HashMap<>();

            assertEquals(taskManager.getAllTasks(), tasksEmptyMap, "Задача не удалена");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска задачи возникла ошибка");
        }
    }
}
