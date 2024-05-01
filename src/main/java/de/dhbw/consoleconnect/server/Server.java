package de.dhbw.consoleconnect.server;

import de.dhbw.consoleconnect.server.account.AccountManager;
import de.dhbw.consoleconnect.server.command.CommandManager;
import de.dhbw.consoleconnect.server.database.DatabaseService;
import de.dhbw.consoleconnect.server.database.h2.H2Database;
import de.dhbw.consoleconnect.server.database.h2.H2DatabaseService;
import de.dhbw.consoleconnect.server.game.GameManager;
import de.dhbw.consoleconnect.server.room.Room;
import de.dhbw.consoleconnect.server.room.RoomManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private final DatabaseService<H2Database> databaseService;
    private final AccountManager accountManager;
    private final CommandManager commandManager;
    private final RoomManager roomManager;
    private final GameManager gameManager;
    private final Map<String, ServerClientThread> clients = new LinkedHashMap<>();

    public Server() {
        this.databaseService = new H2DatabaseService();
        this.accountManager = new AccountManager(this);
        this.commandManager = new CommandManager(this);
        this.roomManager = new RoomManager(this);
        this.gameManager = new GameManager(this);
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
                final Room room = this.roomManager.getRoom(client.getRoomName());
                if (room != null) {
                    room.broadcastMessage(message, false);
                }
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

    public void addClient(final ServerClientThread client) {
        if (client != null) {
            this.clients.put(client.getName(), client);
        }
    }

    public void removeClient(final ServerClientThread client) {
        if (client != null && this.clients.containsValue(client)) {
            this.clients.remove(client.getName());
        }
    }

    public boolean containsClient(final String clientName) {
        if (clientName != null && !clientName.isBlank()) {
            for (final String key : this.clients.keySet()) {
                if (key.equalsIgnoreCase(clientName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public DatabaseService<H2Database> getDatabaseService() {
        return this.databaseService;
    }

    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    public CommandManager getCommandHandler() {
        return this.commandManager;
    }

    public RoomManager getRoomManager() {
        return this.roomManager;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public List<ServerClientThread> getClients() {
        return List.copyOf(this.clients.values());
    }
}
