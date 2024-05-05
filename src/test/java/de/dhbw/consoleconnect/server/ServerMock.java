package de.dhbw.consoleconnect.server;

import de.dhbw.consoleconnect.server.account.AccountManager;
import de.dhbw.consoleconnect.server.command.CommandManager;
import de.dhbw.consoleconnect.server.database.DatabaseService;
import de.dhbw.consoleconnect.server.database.DatabaseServiceMock;
import de.dhbw.consoleconnect.server.database.registry.AccountRepositoryMock;
import de.dhbw.consoleconnect.server.database.registry.GameHistoryRepositoryMock;
import de.dhbw.consoleconnect.server.game.GameManager;
import de.dhbw.consoleconnect.server.room.RoomManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ServerMock implements Server {

    private final DatabaseService<List<?>> databaseService;
    private final AccountManager accountManager;
    private final CommandManager commandManager;
    private final RoomManager roomManager;
    private final GameManager gameManager;
    private final Map<String, ServerClient> clients = new LinkedHashMap<>();

    public ServerMock() {
        this.databaseService = new DatabaseServiceMock();
        this.accountManager = new AccountManager(this, new AccountRepositoryMock());
        this.commandManager = new CommandManager(this);
        this.roomManager = new RoomManager(this);
        this.gameManager = new GameManager(this, new GameHistoryRepositoryMock());
    }

    @Override
    public void start() {
        // Not implemented!
    }

    @Override
    public void broadcastMessage(final ServerClient client, final String message) {
        // Not implemented!
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
    public DatabaseService<List<?>> getDatabaseService() {
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
