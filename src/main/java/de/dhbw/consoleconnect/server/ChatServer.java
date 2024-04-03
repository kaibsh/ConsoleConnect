package de.dhbw.consoleconnect.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatServer {

    private final Map<String, ChatServerThread> clients = new LinkedHashMap<>();

    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("[INFO] Successfully started chat-server!");
            System.out.println("[INFO] The server is listening on port: '" + serverSocket.getLocalPort() + "'");
            while (true) {
                final Socket socket = serverSocket.accept();

                final ChatServerThread chatServerThread = new ChatServerThread(this, socket);
                chatServerThread.start();
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public void broadcastMessage(final String message) {

    }

    public ChatServerThread getClient(final String clientName) {
        for (final Map.Entry<String, ChatServerThread> mapEntry : this.clients.entrySet()) {
            if (mapEntry.getKey().equalsIgnoreCase(clientName)) {
                return mapEntry.getValue();
            }
        }
        return null;
    }

    public void addClient(final String clientName, final ChatServerThread client) {
        this.clients.put(clientName, client);
    }

    public void removeClient(final ChatServerThread client) {
        this.clients.remove(client);
    }

    public boolean containsClient(final String clientName) {
        return this.clients.containsKey(clientName);
    }
}
