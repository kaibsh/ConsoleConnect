package de.dhbw.consoleconnect.server;

import de.dhbw.consoleconnect.server.command.CommandManager;
import de.dhbw.consoleconnect.server.room.RoomManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class Server {

    private final CommandManager commandManager;
    private final RoomManager roomManager;
    private final Map<String, ServerClientThread> clients = new LinkedHashMap<>();

    public Server() {
        this.commandManager = new CommandManager(this);
        this.roomManager = new RoomManager(this);
    }

    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("[INFO] Successfully started chat-server!");
            System.out.println("[INFO] The server is listening on port: '" + serverSocket.getLocalPort() + "'");

            while (true) {
                final Socket socket = serverSocket.accept();

                final ServerClientThread serverClientThread = new ServerClientThread(this, socket);
                serverClientThread.start();
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public void broadcastMessage(final ServerClientThread client, final String message) {
        if (message != null && !message.isBlank()) {
            if (client.getRoomName().equalsIgnoreCase("GLOBAL")) {
                for (final ServerClientThread receiverClient : this.clients.values()) {
                    if (client.getRoomName().equalsIgnoreCase(receiverClient.getRoomName())) {
                        receiverClient.sendMessage(message);
                    }
                }
            } else {
                this.roomManager.getRoom(client.getRoomName()).broadcastMessage(message);
            }
        }
    }

    public void handleMessage(final ServerClientThread client, final String message) {
        if (client != null && message != null && !message.isBlank()) {
            final String trimedMessage = message.trim();
            if (trimedMessage.startsWith("/") && trimedMessage.length() > 1) {
                this.commandManager.handleCommand(client, trimedMessage.substring(1));
            } else {
                this.broadcastMessage(client, client.getClientName() + ": " + trimedMessage);
            }
        }
    }

    public ServerClientThread getClient(final String clientName) {
        if (clientName != null && !clientName.isBlank()) {
            for (final Map.Entry<String, ServerClientThread> mapEntry : this.clients.entrySet()) {
                if (mapEntry.getKey().equalsIgnoreCase(clientName)) {
                    return mapEntry.getValue();
                }
            }
        }
        return null;
    }

    public void addClient(final String clientName, final ServerClientThread client) {
        if (clientName != null && !clientName.isBlank() && client != null) {
            this.clients.put(clientName, client);
        }
    }

    public void removeClient(final ServerClientThread client) {
        if (client != null && this.clients.containsValue(client)) {
            this.clients.remove(client.getClientName());
        }
    }

    public boolean containsClient(final String clientName) {
        if (clientName != null && !clientName.isBlank()) {
            return this.clients.containsKey(clientName);
        }
        return false;
    }

    public CommandManager getCommandHandler() {
        return this.commandManager;
    }

    public RoomManager getRoomManager() {
        return this.roomManager;
    }
}
