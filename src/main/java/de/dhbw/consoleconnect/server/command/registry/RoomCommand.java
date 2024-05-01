package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.command.Command;
import de.dhbw.consoleconnect.server.room.Room;

public class RoomCommand extends Command {

    public RoomCommand() {
        super("room", "Manages private chat-rooms.");
    }

    @Override
    protected void execute(final Server server, final ServerClient client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[RoomCommand] Use '/room help' for more information.");
        } else if (arguments.length == 1) {
            if (arguments[0].equalsIgnoreCase("help")) {
                client.sendMessage("[RoomCommand] Help:");
                client.sendMessage("[RoomCommand] - '/room create <roomName>' | Creates a new room with the given name.");
                client.sendMessage("[RoomCommand] - '/room join <roomName>' | Joins the room with the given name.");
                client.sendMessage("[RoomCommand] - '/room remove' | Removes the current room.");
                client.sendMessage("[RoomCommand] - '/room leave' | Leaves the current room.");
                client.sendMessage("[RoomCommand] - '/room list' | Lists all available rooms.");
            } else if (arguments[0].equalsIgnoreCase("create")) {
                client.sendMessage("[RoomCommand] Usage: /room create <roomName>");
            } else if (arguments[0].equalsIgnoreCase("join")) {
                client.sendMessage("[RoomCommand] Usage: /room join <roomName>");
            } else if (arguments[0].equalsIgnoreCase("remove")) {
                if (!client.getRoomName().equalsIgnoreCase("GLOBAL")) {
                    final Room room = server.getRoomManager().getRoom(client.getRoomName());
                    if (room != null) {
                        server.getRoomManager().removeRoom(room);
                    } else {
                        client.sendMessage("[RoomCommand] An error occurred while removing the room!");
                    }
                } else {
                    client.sendMessage("[RoomCommand] You are already in the global chat-room!");
                }
            } else if (arguments[0].equalsIgnoreCase("leave")) {
                if (!client.getRoomName().equalsIgnoreCase("GLOBAL")) {
                    final Room room = server.getRoomManager().getRoom(client.getRoomName());
                    if (room != null) {
                        server.getRoomManager().leaveRoom(room, false, client);
                        client.sendMessage("[RoomCommand] You have been moved to the global chat-room.");
                    } else {
                        client.sendMessage("[RoomCommand] An error occurred while leaving the room!");
                    }
                } else {
                    client.sendMessage("[RoomCommand] You are already in the global chat-room!");
                }
            } else if (arguments[0].equalsIgnoreCase("list")) {
                if (!server.getRoomManager().getRooms().isEmpty() && server.getRoomManager().calculateBasicRooms() > 0) {
                    client.sendMessage("[RoomCommand] Available rooms:");
                    for (final Room room : server.getRoomManager().getRooms()) {
                        if (!room.isGame()) {
                            final StringBuilder stringBuilder = new StringBuilder();
                            for (final ServerClient clients : room.getClients()) {
                                stringBuilder.append(clients.getName()).append(", ");
                            }
                            stringBuilder.setLength(stringBuilder.length() - 2);
                            client.sendMessage("[RoomCommand] - " + room.getName().replace("ROOM-", "") + " (" + room.getClients().size() + "): " + stringBuilder);
                        }
                    }
                } else {
                    client.sendMessage("[RoomCommand] There are no rooms available!");
                }
            } else {
                client.sendMessage("[RoomCommand] Use '/room help' for more information.");
            }
        } else if (arguments.length == 2) {
            if (arguments[0].equalsIgnoreCase("create")) {
                final String roomName = arguments[1];
                if (!roomName.isBlank()) {
                    if (!roomName.equalsIgnoreCase("GLOBAL")) {
                        if (!server.getRoomManager().isRoomExistent(roomName)) {
                            server.getRoomManager().addRoom(new Room(roomName, false), client);
                        } else {
                            client.sendMessage("[RoomCommand] The room '" + roomName + "' already exists!");
                        }
                    } else {
                        client.sendMessage("[RoomCommand] You cannot create a room with the name 'GLOBAL'!");
                    }
                } else {
                    client.sendMessage("[RoomCommand] Usage: '/room create <roomName>'");
                }
            } else if (arguments[0].equalsIgnoreCase("join")) {
                final String rawRoomName = arguments[1];
                if (!rawRoomName.isBlank()) {
                    final String roomName = "ROOM-" + rawRoomName;
                    if (!roomName.equalsIgnoreCase(client.getRoomName())) {
                        if (!roomName.equalsIgnoreCase("GLOBAL")) {
                            if (server.getRoomManager().isRoomExistent(roomName)) {
                                final Room room = server.getRoomManager().getRoom(roomName);
                                if (room != null) {
                                    if (!room.isGame()) {
                                        server.getRoomManager().joinRoom(room, false, client);
                                        client.sendMessage("[RoomCommand] You have been moved to the private chat-room.");
                                    } else {
                                        client.sendMessage("[RoomCommand] You cannot join a game chat-room!");
                                    }
                                } else {
                                    client.sendMessage("[RoomCommand] An error occurred while joining the room!");
                                }
                            } else {
                                client.sendMessage("[RoomCommand] The room '" + roomName + "' does not exist!");
                            }
                        } else {
                            final Room room = server.getRoomManager().getRoom(client.getRoomName());
                            if (room != null) {
                                server.getRoomManager().leaveRoom(room, false, client);
                                client.sendMessage("[RoomManager] You have been moved to the global chat-room.");
                            } else {
                                client.sendMessage("[RoomCommand] An error occurred while joining the room!");
                            }
                        }
                    } else {
                        if (!roomName.equalsIgnoreCase("GLOBAL")) {
                            client.sendMessage("[RoomCommand] You are already in the room '" + roomName + "'!");
                        } else {
                            client.sendMessage("[RoomCommand] You are already in the global chat-room!");
                        }
                    }
                } else {
                    client.sendMessage("[RoomCommand] Usage: '/room join <roomName>'");
                }
            } else {
                client.sendMessage("[RoomCommand] Use '/room help' for more information.");
            }
        } else {
            client.sendMessage("[RoomCommand] Use '/room help' for more information.");
        }
    }
}
