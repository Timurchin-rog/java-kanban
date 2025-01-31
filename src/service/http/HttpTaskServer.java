package service.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.http.handlers.*;
import service.memory.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final int PORT = 2;
    private static HttpServer httpServer;
    private static TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.startServer();
    }

    public void startServer() {
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
    }

    public int getPORT() {
        return PORT;
    }
}
