package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.command.Command;
import de.dhbw.consoleconnect.server.room.Room;

public class RoomCommand extends Command {

    public RoomCommand() {
        super("room", "TBD");
    }

    @Override
    public void execute(Server server, ServerClientThread client, String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[RoomCommand] Use '/room help' for more information.");
        } else if (arguments.length == 1) {
            if (arguments[0].equalsIgnoreCase("help")) {
                client.sendMessage("[RoomCommand] Help:");
                client.sendMessage("[RoomCommand] - /room create <roomName> | Creates a new room with the given name.");
                client.sendMessage("[RoomCommand] - /room join <roomName> | Joins the room with the given name.");
                client.sendMessage("[RoomCommand] - /room remove | Removes the current room.");
                client.sendMessage("[RoomCommand] - /room leave | Leaves the current room.");
                client.sendMessage("[RoomCommand] - /room list | Lists all available rooms.");
            } else if (arguments[0].equalsIgnoreCase("create")) {
                client.sendMessage("[RoomCommand] Usage: /room create <roomName>");
            } else if (arguments[0].equalsIgnoreCase("join")) {
                client.sendMessage("[RoomCommand] Usage: /room join <roomName>");
            } else if (arguments[0].equalsIgnoreCase("remove")) {
                if (!client.getRoomName().equalsIgnoreCase("GLOBAL")) {
                    server.getRoomManager().removeRoom(server.getRoomManager().getRoom(client.getRoomName()));
                } else {
                    client.sendMessage("[RoomCommand] You are already in the global chat-room!");
                }
            } else if (arguments[0].equalsIgnoreCase("leave")) {
                if (!client.getRoomName().equalsIgnoreCase("GLOBAL")) {
                    server.getRoomManager().leaveRoom(server.getRoomManager().getRoom(client.getRoomName()), client);
                    client.sendMessage("[RoomCommand] You have been moved to the global chat-room.");
                } else {
                    client.sendMessage("[RoomCommand] You are already in the global chat-room!");
                }
            } else if (arguments[0].equalsIgnoreCase("list")) {
                if (!server.getRoomManager().getRooms().isEmpty()) {
                    client.sendMessage("[RoomCommand] Available rooms:");
                    for (final Room room : server.getRoomManager().getRooms()) {
                        if (!room.isGame()) {
                            final StringBuilder stringBuilder = new StringBuilder();
                            for (final ServerClientThread clients : room.getClients()) {
                                stringBuilder.append(clients.getClientName()).append(", ");
                            }
                            stringBuilder.setLength(stringBuilder.length() - 2);
                            client.sendMessage("[RoomCommand] - " + room.getName() + " (" + room.getClients().size() + "): " + stringBuilder);
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
                final String roomName = arguments[1].trim();
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
                    client.sendMessage("[RoomCommand] Usage: /room create <roomName>");
                }
            } else if (arguments[0].equalsIgnoreCase("join")) {
                final String roomName = arguments[1].trim();
                if (!roomName.isBlank()) {
                    if (!roomName.equalsIgnoreCase(client.getRoomName())) {
                        if (!roomName.equalsIgnoreCase("GLOBAL")) {
                            if (server.getRoomManager().isRoomExistent(roomName)) {
                                server.getRoomManager().joinRoom(server.getRoomManager().getRoom(roomName), client);
                                client.sendMessage("[RoomCommand] You have been moved to the private chat-room.");
                            } else {
                                client.sendMessage("[RoomCommand] The room '" + roomName + "' does not exist!");
                            }
                        } else {
                            server.getRoomManager().leaveRoom(server.getRoomManager().getRoom(client.getRoomName()), client);
                            client.sendMessage("[RoomManager] You have been moved to the global chat-room.");
                        }
                    } else {
                        if (!roomName.equalsIgnoreCase("GLOBAL")) {
                            client.sendMessage("[RoomCommand] You are already in the room '" + roomName + "'!");
                        } else {
                            client.sendMessage("[RoomCommand] You are already in the global chat-room!");
                        }
                    }
                } else {
                    client.sendMessage("[RoomCommand] Usage: /room join <roomName>");
                }
            } else {
                client.sendMessage("[RoomCommand] Use '/room help' for more information.");
            }
        } else {
            client.sendMessage("[RoomCommand] Use '/room help' for more information.");
        }
    }
}
