package org.example.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class StaticResourceHandler implements HttpHandler {
    private static final Map<String, String> RESOURCE_TYPES = Map.of(
            "/", "text/html; charset=utf-8",
            "/index.html", "text/html; charset=utf-8",
            "/style.css", "text/css; charset=utf-8",
            "/app.js", "text/javascript; charset=utf-8");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String contentType = RESOURCE_TYPES.get(exchange.getRequestURI().getPath());
        if (contentType == null || !exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(contentType == null ? 404 : 405, -1);
            exchange.close();
            return;
        }
        String resourceName = exchange.getRequestURI().getPath().equals("/") ? "/web/index.html"
                : "/web" + exchange.getRequestURI().getPath();
        try (InputStream resource = StaticResourceHandler.class.getResourceAsStream(resourceName)) {
            if (resource == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            byte[] content = resource.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");
            exchange.sendResponseHeaders(200, content.length);
            exchange.getResponseBody().write(content);
        } finally {
            exchange.close();
        }
    }
}
