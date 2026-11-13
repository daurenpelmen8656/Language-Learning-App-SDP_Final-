package org.example.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class WebApiHandler implements HttpHandler {
    private static final int MAX_REQUEST_BYTES = 16_384;
    private final WebApi api;

    public WebApiHandler(WebApi api) {
        this.api = api;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            Object response = switch (path) {
                case "/api/lesson" -> handleGetLesson(method);
                case "/api/progress" -> handleGetProgress(method);
                case "/api/events" -> handleGetEvents(method);
                case "/api/answer" -> handleAnswer(exchange, method);
                case "/api/strategy" -> handleStrategy(exchange, method);
                default -> throw new NotFoundException();
            };
            sendJson(exchange, 200, response);
        } catch (NotFoundException exception) {
            sendJson(exchange, 404, Map.of("error", "Not found"));
        } catch (IllegalArgumentException exception) {
            sendJson(exchange, 400, Map.of("error", exception.getMessage()));
        } catch (MethodNotAllowedException exception) {
            exchange.getResponseHeaders().set("Allow", exception.allowedMethod);
            sendJson(exchange, 405, Map.of("error", "Method not allowed"));
        } catch (IOException exception) {
            sendJson(exchange, 500, Map.of("error", "Request could not be completed"));
        } finally {
            exchange.close();
        }
    }

    private Object handleAnswer(HttpExchange exchange, String method) throws IOException {
        requireRequestMethod(method, "POST");
        Map<String, Object> payload = readPayload(exchange);
        Object word = payload.get("word");
        Object correct = payload.get("correct");
        if (!(word instanceof String wordText) || !(correct instanceof Boolean isCorrect)) {
            throw new IllegalArgumentException("Expected string word and boolean correct");
        }
        return api.submitAnswer(wordText, isCorrect);
    }

    private Object handleStrategy(HttpExchange exchange, String method) throws IOException {
        requireRequestMethod(method, "POST");
        Object name = readPayload(exchange).get("name");
        if (!(name instanceof String strategyName)) {
            throw new IllegalArgumentException("Expected string name");
        }
        return api.switchStrategy(strategyName);
    }

    private Map<String, Object> readPayload(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readNBytes(MAX_REQUEST_BYTES + 1);
        if (bytes.length > MAX_REQUEST_BYTES) {
            throw new IllegalArgumentException("Request body is too large");
        }
        return JsonCodec.parseObject(new String(bytes, StandardCharsets.UTF_8));
    }

    private Object handleGetLesson(String method) {
        requireRequestMethod(method, "GET");
        return api.getLesson();
    }

    private Object handleGetProgress(String method) {
        requireRequestMethod(method, "GET");
        return api.getProgress();
    }

    private Object handleGetEvents(String method) {
        requireRequestMethod(method, "GET");
        return api.getEvents();
    }

    private void requireRequestMethod(String actualMethod, String expectedMethod) {
        if (!actualMethod.equalsIgnoreCase(expectedMethod)) {
            throw new MethodNotAllowedException(expectedMethod);
        }
    }

    private void sendJson(HttpExchange exchange, int status, Object value) throws IOException {
        byte[] body = JsonCodec.stringify(value).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
    }

    private static final class NotFoundException extends RuntimeException { }

    private static final class MethodNotAllowedException extends RuntimeException {
        private final String allowedMethod;

        private MethodNotAllowedException(String allowedMethod) {
            this.allowedMethod = allowedMethod;
        }
    }
}
