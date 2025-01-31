package service.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import service.http.Endpoint;
import service.memory.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        if (endpoint.equals(Endpoint.GET_PRIORITIZED_TASKS))
            handleGetHistory(exchange);
        else
            writeResponse(exchange, "Такого эндпоинта не существует", 405);
    }

    private Endpoint getEndpoint(String requestMethod) {
        if (requestMethod.equals("GET"))
            return Endpoint.GET_PRIORITIZED_TASKS;
        else
            return Endpoint.UNKNOWN;
    }

    public void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = taskManager.getPrioritizedTasks().toString();
        writeResponse(exchange, response, 200);
    }
}