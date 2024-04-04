package de.dhbw.consoleconnect.server.room;

import de.dhbw.consoleconnect.server.ServerClientThread;

import java.util.LinkedList;
import java.util.List;

public class Room {

    private final String name;
    private final boolean game;
    private final List<ServerClientThread> clients = new LinkedList<>();

    public Room(final String name, final boolean game) {
        this.name = name;
        this.game = game;
    }

    public void broadcastMessage(final String message) {
        if (message != null && !message.isBlank()) {
            for (final ServerClientThread serverClientThread : this.clients) {
                serverClientThread.sendMessage(message);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isGame() {
        return this.game;
    }

    public List<ServerClientThread> getClients() {
        return List.copyOf(this.clients);
    }

    protected void addClient(final ServerClientThread client, final boolean silent) {
        if (client != null) {
            if (!silent) {
                this.broadcastMessage("-> " + client.getClientName() + " has entered the chat-room!");
            }
            this.clients.add(client);
            client.setRoomName(this.name);
            client.sendMessage("[RoomManager] ENTER: " + this.name);
        }
    }

    protected void removeClient(final ServerClientThread client, final boolean silent) {
        if (client != null && this.clients.contains(client)) {
            this.clients.remove(client);
            client.setRoomName("GLOBAL");
            client.sendMessage("[RoomManager] LEAVE: GLOBAL");
            if (!silent) {
                this.broadcastMessage("<- " + client.getClientName() + " has left the chat-room!");
            }
        }
    }

    public boolean isEmtpy() {
        return this.clients.isEmpty();
    }
}
