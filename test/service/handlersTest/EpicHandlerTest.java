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

@DisplayName("Обработчик эпиков HTTP-сервера")
public class EpicHandlerTest {

    HttpTaskServer httpTaskServer;
    Gson gson;
    TaskManager taskManager;
    HttpClient client;
    Subtask subtask;
    Epic epic;
    String epicJson;
    String subtaskJson;

    @BeforeEach
    void start() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = httpTaskServer.getGson();
        httpTaskServer.startServer();
        client = HttpClient.newHttpClient();
        epic = taskManager.createEpic(new Epic(Type.EPIC, "Test Epic", "Test Epic Description"));
        epicJson = gson.toJson(epic);
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
    @DisplayName("Должен отправлять клиенту все эпики")
    void shouldSendToClientAllEpics() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус при получении эпиков");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска эпиков возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен отправлять клиенту эпик по ID")
    void shouldSendToClientEpicByID() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус при получении эпика");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска эпиков возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен выбрасывать NotFoundException")
    void shouldThrowNotFoundException() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/epics/" + epic.getId() + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Неверный код при поиске несуществующего эпика");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска эпика возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен отправлять клиенту все подзадачи эпика")
    void shouldSendToClientAllSubtasksOfEpic() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус при получении подзадач эпика");
            assertEquals(taskManager.getAllSubtasks().size(), 1, "В мапе нет подзадач");
            assertEquals(taskManager.getSubtasksOfEpic(epic.getId()).size(), 1, "В списке подзадач эпика пусто");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска подзадач эпика возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен добавлять эпик")
    void shouldAddEpics() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при добавлении эпика");
            HashMap<Integer, Epic> epicsFromManager = taskManager.getAllEpics();

            assertNotNull(epicsFromManager, "Эпики не возвращаются при добавлении");
            assertEquals(2, epicsFromManager.size(), "Некорректное количество эпиков при добавлении");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время добавления эпика возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен обновлять эпик по ID")
    void shouldUpdateEpicByID() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/epics/" + epic.getId());
        Epic epicUpd = new Epic(Type.EPIC, "Test EpicUpd", "Test EpicUpd description");
        String epicUpdJson = gson.toJson(epicUpd);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicUpdJson)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при обновлении эпика");

            HashMap<Integer, Epic> epicsFromManager = taskManager.getAllEpics();

            assertNotNull(epicsFromManager, "Эпики не возвращаются при обновлении");
            assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков при обновлении");
            assertEquals("Test EpicUpd", epicsFromManager.get(epic.getId()).getName(),
                    "Не обновилось имя эпика");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время обновления эпика возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен удалять эпик по ID")
    void shouldDeleteEpicByID() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при удалении эпика");

            HashMap<Integer, Epic> epicsEmptyMap = new HashMap<>();

            assertEquals(taskManager.getAllTasks(), epicsEmptyMap, "Эпик не удалён");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска эпика возникла ошибка");
        }
    }

    @Test
    @DisplayName("Должен удалять все эпики")
    void shouldDeleteAllEpics() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Неверный статус при удалении всех эпиков");

            HashMap<Integer, Epic> epicsEmptyMap = new HashMap<>();

            assertEquals(taskManager.getAllEpics(), epicsEmptyMap, "Эпик не удалён");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время поиска эпика возникла ошибка");
        }
    }
}

