package de.dhbw.consoleconnect.server.room;

import de.dhbw.consoleconnect.server.ServerClient;

import java.util.LinkedList;
import java.util.List;

public class Room {

    private final String name;
    private final String displayName;
    private final boolean game;
    private final List<ServerClient> clients = new LinkedList<>();

    public Room(final String name, final boolean game) {
        this.name = game ? "GAME-" + name : "ROOM-" + name;
        this.displayName = name;
        this.game = game;
    }

    public Room(final String name, final String displayName, final boolean game) {
        this.name = game ? "GAME-" + name : "ROOM-" + name;
        this.displayName = displayName;
        this.game = game;
    }

    public void broadcastMessage(final String message, final boolean force) {
        if (message != null && !message.isBlank()) {
            if (!this.game || force) {
                for (final ServerClient serverClient : this.clients) {
                    serverClient.sendMessage(message);
                }
            }
        }
    }

    protected void addClient(final ServerClient client) {
        if (client != null) {
            this.clients.add(client);
            client.setRoomName(this.name);
            client.sendMessage("[HOOK] ROOM ->: " + this.displayName);
        }
    }

    protected void removeClient(final ServerClient client) {
        if (client != null && this.clients.contains(client)) {
            this.clients.remove(client);
            client.setRoomName("GLOBAL");
            client.sendMessage("[HOOK] ROOM <-: GLOBAL");
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isGame() {
        return this.game;
    }

    public List<ServerClient> getClients() {
        return List.copyOf(this.clients);
    }

    public boolean isEmtpy() {
        return this.clients.isEmpty();
    }
}
