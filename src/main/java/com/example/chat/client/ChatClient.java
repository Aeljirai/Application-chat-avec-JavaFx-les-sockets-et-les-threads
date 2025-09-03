package com.example.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread readerThread;

    public void connect(String host, int port, String username, Consumer<String> onMessage) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        out.println(username);

        readerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    onMessage.accept(line);
                }
            } catch (IOException ignored) {
            } finally {
                onMessage.accept("** Connexion fermée **");
            }
        }, "reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void send(String message) {
        if (out != null && message != null && !message.isBlank()) {
            out.println(message);
        }
    }

    public void close() {
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}
