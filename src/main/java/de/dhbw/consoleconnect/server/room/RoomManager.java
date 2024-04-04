package de.dhbw.consoleconnect.server.room;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;

import java.util.LinkedList;
import java.util.List;

public class RoomManager {

    private final Server server;
    private final List<Room> rooms = new LinkedList<>();

    public RoomManager(final Server server) {
        this.server = server;
    }

    public void joinRoom(final Room room, final ServerClientThread client) {
        if (room != null && client != null && this.rooms.contains(room) && !client.getRoomName().equals(room.getName())) {
            final Room currentRoom = this.getRoom(client.getRoomName());
            if (currentRoom != null) {
                this.leaveRoom(currentRoom, client);
            }
            room.addClient(client, false);
            System.out.println("[ROOM] Client '" + client.getClientName() + "' has joined the room '" + room.getName() + "'.");
        }
    }

    public void leaveRoom(final Room room, final ServerClientThread client) {
        if (room != null && client != null && this.rooms.contains(room) && client.getRoomName().equals(room.getName())) {
            room.removeClient(client, false);
            System.out.println("[ROOM] Client '" + client.getClientName() + "' has left the room '" + room.getName() + "'.");
            if (room.isEmtpy()) {
                this.removeRoom(room);
            }
        }
    }

    public void addRoom(final Room room, final ServerClientThread client) {
        if (room != null && client != null && !this.rooms.contains(room)) {
            this.rooms.add(room);
            room.addClient(client, true);
            room.broadcastMessage("[RoomManager] The room has been added! You have been moved to the private chat-room.");
            System.out.println("[ROOM] Room '" + room.getName() + "' has been added by '" + client.getClientName() + "'.");
        }
    }

    public void removeRoom(final Room room) {
        if (room != null && this.rooms.contains(room)) {
            this.rooms.remove(room);
            if (!room.isEmtpy()) {
                room.broadcastMessage("[RoomManager] The room has been removed! You have been moved to the global chat-room.");
                for (final ServerClientThread client : room.getClients()) {
                    room.removeClient(client, true);
                    System.out.println("[ROOM] Client '" + client.getClientName() + "' has left the room '" + room.getName() + "'.");
                }
            }
            System.out.println("[ROOM] Room '" + room.getName() + "' has been removed.");
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

    public List<Room> getRooms() {
        return List.copyOf(this.rooms);
    }
}
