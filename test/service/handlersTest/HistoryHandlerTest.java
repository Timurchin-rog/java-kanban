package service.handlersTest;

import com.google.gson.Gson;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Обработчик истории HTTP-сервера")
public class HistoryHandlerTest {

    HttpTaskServer httpTaskServer;
    Gson gson;
    TaskManager taskManager;
    HttpClient client;

    @BeforeEach
    void start() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = httpTaskServer.getGson();
        httpTaskServer.startServer();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void stop() {
        httpTaskServer.stopServer();
    }

    @Test
    @DisplayName("Должен отправлять клиенту историю просмотров")
    void shouldSendToClientAllEpics() {
        URI url = URI.create("http://localhost:" + httpTaskServer.getPORT() + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Неверный статус при получении истории просмотров");
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время получения истории просмотров возникла ошибка");
        }
    }
}


