package service.http.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
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

public class SubtaskHandler extends BaseHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET_SUBTASK: {
                handleGetSubtask(exchange);
                break;
            }
            case POST_CREATE_SUBTASK: {
                handleCreateSubtask(exchange);
                break;
            }
            case POST_UPDATE_SUBTASK: {
                handleUpdateSubtask(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                handleDeleteSubtask(exchange);
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
                if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                    return Endpoint.GET_SUBTASKS;
                } else {
                    return Endpoint.GET_SUBTASK;
                }
            }
            case "POST": {
                if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                    return Endpoint.POST_CREATE_SUBTASK;
                } else {
                    return Endpoint.POST_UPDATE_SUBTASK;
                }
            }
            case "DELETE": {
                return Endpoint.DELETE_SUBTASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private Subtask getSubtaskFromRequest(HttpExchange exchange) throws IOException {
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
        int epicId = jsonObject.get("epicId").getAsInt();
        return new Subtask(Type.SUBTASK, name, status, description, duration, startTime, epicId);
    }

    public void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = taskManager.getAllSubtasks().toString();
        writeResponse(exchange, response, 200);
    }

    public void handleGetSubtask(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        try {
            String response = taskManager.getSubtask(id).toString();
            writeResponse(exchange, response, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }

    public void handleCreateSubtask(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = getSubtaskFromRequest(exchange);
            taskManager.createSubtask(subtask);
            writeResponse(exchange, "Подзадача с идентификатором " + subtask.getId() + " создана", 201);
        } catch (ValidationException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    public void handleUpdateSubtask(HttpExchange exchange) throws IOException {
        try {
            Subtask subtask = getSubtaskFromRequest(exchange);
            taskManager.updateSubtask(getTaskId(exchange), subtask);
            writeResponse(exchange, "Подзадача с идентификатором " + subtask.getId() + " обновлена", 201);
        } catch (ValidationException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    public void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        taskManager.removeSubtask(id);
        writeResponse(exchange, "Подзадача с идентификатором " + id + " удалена", 201);
    }
}