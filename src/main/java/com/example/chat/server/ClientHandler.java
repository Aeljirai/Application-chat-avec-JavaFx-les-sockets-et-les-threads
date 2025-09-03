package com.example.chat.server;

import java.io.*;
import java.net.Socket;

class ClientHandler implements Runnable {
    private final ChatServer server;
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username = "Anonyme";

    ClientHandler(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            String first = in.readLine();
            if (first != null && !first.isBlank()) {
                username = first.trim();
            }
            server.broadcast("** " + username + " a rejoint le chat **", this);

            String line;
            while ((line = in.readLine()) != null) {
                String msg = username + ": " + line;
                server.broadcast(msg, this);
            }
        } catch (IOException ignored) {
        } finally {
            server.remove(this);
            server.broadcast("** " + username + " s'est déconnecté **", this);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    void send(String message) {
        if (out != null) out.println(message);
    }
}
