package de.dhbw.consoleconnect.server;

import de.dhbw.consoleconnect.server.account.AccountManager;
import de.dhbw.consoleconnect.server.command.CommandManager;
import de.dhbw.consoleconnect.server.database.DatabaseService;
import de.dhbw.consoleconnect.server.database.h2.H2DatabaseService;
import de.dhbw.consoleconnect.server.database.h2.registry.H2AccountRepository;
import de.dhbw.consoleconnect.server.database.h2.registry.H2GameHistoryRepository;
import de.dhbw.consoleconnect.server.game.GameManager;
import de.dhbw.consoleconnect.server.room.Room;
import de.dhbw.consoleconnect.server.room.RoomManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SocketServer implements Server {

    private final DatabaseService<Connection> databaseService;
    private final AccountManager accountManager;
    private final CommandManager commandManager;
    private final RoomManager roomManager;
    private final GameManager gameManager;
    private final Map<String, ServerClient> clients = new LinkedHashMap<>();

    public SocketServer() {
        this.databaseService = new H2DatabaseService();
        this.accountManager = new AccountManager(this, new H2AccountRepository());
        this.commandManager = new CommandManager(this);
        this.roomManager = new RoomManager(this);
        this.gameManager = new GameManager(this, new H2GameHistoryRepository());
    }

    @Override
    public void start() {
        try (final ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("[INFO] Successfully started chat-server!");
            System.out.println("[INFO] The server is listening on port: '" + serverSocket.getLocalPort() + "'");
            while (true) {
                final Socket socket = serverSocket.accept();
                final ThreadServerClient threadServerClient = new ThreadServerClient(this, socket);
                threadServerClient.start();
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void broadcastMessage(final ServerClient client, final String message) {
        if (message != null && !message.isBlank()) {
            if (client.getRoomName().equalsIgnoreCase("GLOBAL")) {
                for (final ServerClient receiverClient : this.clients.values()) {
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

    @Override
    public ServerClient getClient(final String clientName) {
        if (clientName != null && !clientName.isBlank()) {
            for (final Map.Entry<String, ServerClient> mapEntry : this.clients.entrySet()) {
                if (mapEntry.getKey().equalsIgnoreCase(clientName)) {
                    return mapEntry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void addClient(final ServerClient client) {
        if (client != null) {
            this.clients.put(client.getName(), client);
        }
    }

    @Override
    public void removeClient(final ServerClient client) {
        if (client != null && this.clients.containsValue(client)) {
            this.clients.remove(client.getName());
        }
    }

    @Override
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

    @Override
    public DatabaseService<Connection> getDatabaseService() {
        return this.databaseService;
    }

    @Override
    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    @Override
    public CommandManager getCommandHandler() {
        return this.commandManager;
    }

    @Override
    public RoomManager getRoomManager() {
        return this.roomManager;
    }

    @Override
    public GameManager getGameManager() {
        return this.gameManager;
    }

    @Override
    public List<ServerClient> getClients() {
        return List.copyOf(this.clients.values());
    }
}
