package org.example.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public final class WebServer {
    private final HttpServer server;

    public WebServer(WebApi api, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/api/", new WebApiHandler(api));
        server.createContext("/", new StaticResourceHandler());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
