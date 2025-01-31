package service.http.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Type;
import service.http.Endpoint;
import service.memory.NotFoundException;
import service.memory.TaskManager;
import service.memory.ValidationException;

import java.io.IOException;
import java.io.InputStream;

public class EpicHandler extends BaseHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_EPIC: {
                handleGetEpic(exchange);
                break;
            }
            case GET_EPIC_SUBTASKS: {
                handleGetSubtaskOfEpic(exchange);
                break;
            }
            case POST_CREATE_EPIC: {
                handleCreateEpic(exchange);
                break;
            }
            case POST_UPDATE_EPIC: {
                handleUpdateEpic(exchange);
                break;
            }
            case DELETE_EPICS: {
                handleDeleteEpics(exchange);
                break;
            }
            case DELETE_EPIC: {
                handleDeleteEpic(exchange);
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
                if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                    return Endpoint.GET_EPICS;
                } else if (pathParts.length == 4 && pathParts[1].equals("epics")) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                } else {
                    return Endpoint.GET_EPIC;
                }
            }
            case "POST": {
                if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                    return Endpoint.POST_CREATE_EPIC;
                } else {
                    return Endpoint.POST_UPDATE_EPIC;
                }
            }
            case "DELETE": {
                if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                    return Endpoint.DELETE_EPICS;
                } else {
                    return Endpoint.DELETE_EPIC;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

    private Epic getEpicFromRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        return new Epic(Type.EPIC, name, description);
    }

    public void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = taskManager.getAllEpics().toString();
        writeResponse(exchange, response, 200);
    }

    public void handleGetEpic(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        try {
            String response = taskManager.getEpic(id).toString();
            writeResponse(exchange, response, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }

    public void handleGetSubtaskOfEpic(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        try {
            String response = taskManager.getSubtasksOfEpic(id).toString();
            writeResponse(exchange, response, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        }
    }

    public void handleCreateEpic(HttpExchange exchange) throws IOException {
        try {
            Epic epic = getEpicFromRequest(exchange);
            taskManager.createEpic(epic);
            writeResponse(exchange, String.format("Эпик с идентификатором %d создан", epic.getId()), 201);
        } catch (ValidationException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    public void handleUpdateEpic(HttpExchange exchange) throws IOException {
        try {
            Epic epic = getEpicFromRequest(exchange);
            taskManager.updateEpic(getTaskId(exchange), epic);
            writeResponse(exchange, String.format("Эпик с идентификатором %d обновлён", epic.getId()), 201);
        } catch (ValidationException e) {
            writeResponse(exchange, e.getMessage(), 406);
        }
    }

    public void handleDeleteEpics(HttpExchange exchange) throws IOException {
        taskManager.removeAllEpics();
        writeResponse(exchange, "Эпики удалены", 201);
    }

    public void handleDeleteEpic(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        taskManager.removeEpic(id);
        writeResponse(exchange, String.format("Эпик с идентификатором %d удалён", id), 201);
    }
}