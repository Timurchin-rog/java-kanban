package service.http.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import model.Type;
import service.http.Endpoint;
import service.memory.NotFoundException;
import service.memory.TaskManager;
import service.memory.ValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskHandler extends BaseHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_TASK: {
                handleGetTask(exchange);
                break;
            }
            case POST_CREATE_TASK: {
                handleCreateTask(exchange);
                break;
            }
            case POST_UPDATE_TASK: {
                handleUpdateTask(exchange);
                break;
            }
            case DELETE_TASKS: {
                handleDeleteTasks(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 405);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET": {
                if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                    return Endpoint.GET_TASKS;
                } else {
                    return Endpoint.GET_TASK;
                }
            }
            case "POST": {
                if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                    return Endpoint.POST_CREATE_TASK;
                } else {
                    return Endpoint.POST_UPDATE_TASK;
                }
            }
            case "DELETE": {
                if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                    return Endpoint.DELETE_TASKS;
                } else {
                    return Endpoint.DELETE_TASK;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

    private Task getTaskFromRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        String name = jsonObject.get("name").getAsString();
        String status = jsonObject.get("status").getAsString();
        String description = jsonObject.get("description").getAsString();
        int durationInt = jsonObject.get("duration").getAsInt();
        Duration duration = Duration.ofMinutes(durationInt);
        String startTimeStr = jsonObject.get("startTime").getAsString();
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        return new Task(Type.TASK, name, status, description, duration, startTime);
    }

    public void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = taskManager.getAllTasks().toString();
        writeResponse(exchange, response, 200);
    }

    public void handleGetTask(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        try {
            String response = taskManager.getTask(id).toString();
            writeResponse(exchange, response, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }

    public void handleCreateTask(HttpExchange exchange) throws IOException {
        try {
            Task task = getTaskFromRequest(exchange);
            taskManager.createTask(task);
            writeResponse(exchange, String.format("Задача с идентификатором %d создана", task.getId()), 201);
        } catch (ValidationException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    public void handleUpdateTask(HttpExchange exchange) throws IOException {
        try {
            Task task = getTaskFromRequest(exchange);
            taskManager.updateTask(getTaskId(exchange), task);
            writeResponse(exchange, String.format("Задача с идентификатором %d обновлена", task.getId()), 201);
        } catch (ValidationException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    public void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.removeAllTasks();
        writeResponse(exchange, "Задачи удалены", 201);
    }

    public void handleDeleteTask(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        taskManager.removeTask(id);
        writeResponse(exchange, String.format("Задача с идентификатором %d удалена", id), 201);
    }
}
