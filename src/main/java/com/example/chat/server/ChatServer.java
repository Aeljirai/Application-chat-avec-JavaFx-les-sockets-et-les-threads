package com.example.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private final int port;
    final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public ChatServer(int port) { this.port = port; }

    public void start() throws IOException {
        System.out.println("[Server] Starting on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(this, socket);
                clients.add(handler);
                new Thread(handler, "client-" + socket.getPort()).start();
            }
        }
    }

    void broadcast(String message, ClientHandler except) {
        for (ClientHandler c : clients) {
            if (c != except) c.send(message);
        }
        System.out.println("[Broadcast] " + message);
    }

    void remove(ClientHandler handler) {
        clients.remove(handler);
    }

    public static void main(String[] args) {
        int port = 5555;
        if (args.length == 1) port = Integer.parseInt(args[0]);
        try {
            new ChatServer(port).start();
        } catch (IOException e) {
            System.err.println("[Server] Fatal: " + e.getMessage());
        }
    }
}
