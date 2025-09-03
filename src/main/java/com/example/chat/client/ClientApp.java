package com.example.chat.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ClientApp extends Application {
    private final ChatClient client = new ChatClient();
    private TextArea chatArea;
    private TextField inputField, hostField, portField, userField;
    private Button sendBtn, connectBtn;

    @Override public void start(Stage stage) {
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        inputField = new TextField();
        inputField.setPromptText("Votre message…");
        inputField.setDisable(true);

        sendBtn = new Button("Envoyer");
        sendBtn.setDisable(true);

        hostField = new TextField("127.0.0.1");
        portField = new TextField("5555");
        userField = new TextField("Etudiant");

        connectBtn = new Button("Connexion");

        HBox top = new HBox(8,
                new Label("Hôte:"), hostField,
                new Label("Port:"), portField,
                new Label("Utilisateur:"), userField,
                connectBtn);
        top.setPadding(new Insets(10));

        HBox bottom = new HBox(8, inputField, sendBtn);
        bottom.setPadding(new Insets(10));
        HBox.setHgrow(inputField, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(chatArea);
        root.setBottom(bottom);

        connectBtn.setOnAction(e -> connect());
        sendBtn.setOnAction(e -> send());
        inputField.setOnAction(e -> send());

        stage.setTitle("ChatFX - Client");
        stage.setScene(new Scene(root, 800, 500));
        stage.show();

        stage.setOnCloseRequest(e -> client.close());
    }

    private void connect() {
        String host = hostField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());
        String user = userField.getText().trim().isEmpty() ? "Etudiant" : userField.getText().trim();

        try {
            client.connect(host, port, user, this::appendMessage);
            appendMessage("** Connecté à " + host + ":" + port + " en tant que " + user + " **");
            hostField.setDisable(true);
            portField.setDisable(true);
            userField.setDisable(true);
            connectBtn.setDisable(true);
            inputField.setDisable(false);
            sendBtn.setDisable(false);
            inputField.requestFocus();
        } catch (Exception ex) {
            appendMessage("** Erreur de connexion: " + ex.getMessage() + " **");
        }
    }

    private void send() {
        String msg = inputField.getText();
        if (msg == null || msg.isBlank()) return;
        client.send(msg);
        appendMessage("(moi) " + msg);
        inputField.clear();
    }

    private void appendMessage(String m) {
        Platform.runLater(() -> {
            if (!chatArea.getText().isEmpty()) chatArea.appendText("\n");
            chatArea.appendText(m);
        });
    }

    public static void main(String[] args) { launch(args); }
}
