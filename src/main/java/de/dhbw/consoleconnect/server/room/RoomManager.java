package de.dhbw.consoleconnect.server.room;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.game.Game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class RoomManager {

    private final Server server;
    private final List<Room> rooms = new LinkedList<>();

    public RoomManager(final Server server) {
        this.server = server;
    }

    public void joinRoom(final Room room, final boolean silent, final ServerClient client) {
        if (room != null && client != null && this.rooms.contains(room) && !client.getRoomName().equals(room.getName())) {
            if (!room.getClients().contains(client)) {
                final Room currentRoom = this.getRoom(client.getRoomName());
                if (currentRoom != null) {
                    this.leaveRoom(currentRoom, false, client);
                }
                room.addClient(client);
                if (!silent) {
                    room.broadcastMessage("-> " + client.getName() + " has entered the chat-room!", true);
                }
                System.out.println("[ROOM] Client '" + client.getName() + "' has joined the room '" + room.getName() + "'.");
            }
        }
    }

    public void leaveRoom(final Room room, final boolean silent, final ServerClient client) {
        if (room != null && client != null && this.rooms.contains(room) && client.getRoomName().equals(room.getName())) {
            if (room.getClients().contains(client)) {
                if (room.isGame()) {
                    final Game game = this.server.getGameManager().getGame(client);
                    if (game != null && game.isRunning()) {
                        if (!silent) {
                            room.broadcastMessage("<- " + client.getName() + " has left the chat-room!", true);
                        }
                        this.server.getGameManager().stopGame(game);
                        return;
                    }
                }
                System.out.println("[ROOM] Client '" + client.getName() + "' has left the room '" + room.getName() + "'.");
                if (!silent) {
                    room.broadcastMessage("<- " + client.getName() + " has left the chat-room!", true);
                }
                room.removeClient(client);
                if (room.isEmtpy() && !room.isGame()) {
                    this.removeRoom(room);
                }
            }
        }
    }

    public void addRoom(final Room room, final ServerClient... clients) {
        if (room != null && clients != null && clients.length > 0 && !this.rooms.contains(room)) {
            this.rooms.add(room);
            for (final ServerClient client : clients) {
                final Room currentRoom = this.getRoom(client.getRoomName());
                if (currentRoom != null) {
                    this.leaveRoom(currentRoom, false, client);
                }
                room.addClient(client);
            }
            System.out.println("[ROOM] Room '" + room.getName() + "' has been added by '" + String.join("', '", Arrays.stream(clients).map(ServerClient::getName).toList()) + "'.");
            room.broadcastMessage("[RoomManager] The room has been added! You have been moved to the private chat-room.", true);
        }
    }

    public void removeRoom(final Room room) {
        if (room != null && this.rooms.contains(room)) {
            if (!room.isEmtpy()) {
                room.broadcastMessage("[RoomManager] The room has been removed! You have been moved to the global chat-room.", true);
                for (final ServerClient client : room.getClients()) {
                    this.leaveRoom(room, true, client);
                }
            } else {
                this.rooms.remove(room);
                System.out.println("[ROOM] Room '" + room.getName() + "' has been removed.");
            }
        }
    }

    public boolean isRoomExistent(final String roomName) {
        if (roomName != null && !roomName.isBlank()) {
            for (final Room room : this.rooms) {
                if (room.getName().equalsIgnoreCase(roomName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Room getRoom(final String roomName) {
        if (roomName != null && !roomName.isBlank()) {
            for (final Room room : this.rooms) {
                if (room.getName().equalsIgnoreCase(roomName)) {
                    return room;
                }
            }
        }
        return null;
    }

    public int getNonGameRoomAmount() {
        int gameRooms = 0;
        for (final Room room : this.rooms) {
            if (room.isGame()) {
                gameRooms++;
            }
        }
        return this.getRooms().size() - gameRooms;
    }

    public List<Room> getRooms() {
        return List.copyOf(this.rooms);
    }
}
