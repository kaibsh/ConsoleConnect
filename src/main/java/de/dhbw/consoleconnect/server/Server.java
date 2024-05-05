package de.dhbw.consoleconnect.server;

import de.dhbw.consoleconnect.server.account.AccountManager;
import de.dhbw.consoleconnect.server.command.CommandManager;
import de.dhbw.consoleconnect.server.database.DatabaseService;
import de.dhbw.consoleconnect.server.game.GameManager;
import de.dhbw.consoleconnect.server.room.RoomManager;

import java.util.List;

public interface Server {

    void start();

    void broadcastMessage(final ServerClient client, final String message);

    ServerClient getClient(final String clientName);

    void addClient(final ServerClient client);

    void removeClient(final ServerClient client);

    boolean containsClient(final String clientName);

    DatabaseService<?> getDatabaseService();

    AccountManager getAccountManager();

    CommandManager getCommandHandler();

    RoomManager getRoomManager();

    GameManager getGameManager();

    List<ServerClient> getClients();
}
