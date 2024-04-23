package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.command.Command;
import de.dhbw.consoleconnect.server.game.GameMode;
import de.dhbw.consoleconnect.server.game.GameRequest;
import de.dhbw.consoleconnect.server.room.Room;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameCommand extends Command {

    private final Map<Integer, GameMode> gameModes = new LinkedHashMap<>();

    public GameCommand() {
        super("game", "Manages private games.");
        for (int i = 1; i <= GameMode.values().length; i++) {
            this.gameModes.put(i, GameMode.values()[i - 1]);
        }
    }

    @Override
    protected void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[GameCommand] Use '/game help' for more information.");
        } else if (arguments.length == 1) {
            if (arguments[0].equalsIgnoreCase("help")) {
                client.sendMessage("[GameCommand] Help:");
                client.sendMessage("[GameCommand] - '/game invite <gameMode> <clientName>' | Sends a game request to the specified client.");
                client.sendMessage("[GameCommand] - '/game cancel <clientName>' | Cancels a game request to the specified client.");
                client.sendMessage("[GameCommand] - '/game accept <clientName>' | Accepts a game request from the specified client.");
                client.sendMessage("[GameCommand] - '/game deny <clientName>' | Denies a game request from the specified client.");
                client.sendMessage("[GameCommand] - '/game requests (<SENDED ? RECEIVED>)' | Lists all game requests.");
                client.sendMessage("[GameCommand] - '/game modes' | Lists all available game modes.");
            } else if (arguments[0].equalsIgnoreCase("requests")) {
                final List<GameRequest> sendedGameRequests = server.getGameManager().getSendedGameRequests(client);
                final List<GameRequest> receivedGameRequests = server.getGameManager().getReceivedGameRequests(client);
                if (!sendedGameRequests.isEmpty() || !receivedGameRequests.isEmpty()) {
                    if (!sendedGameRequests.isEmpty()) {
                        client.sendMessage("[GameCommand] Sent game requests:");
                        for (final GameRequest gameRequest : sendedGameRequests) {
                            client.sendMessage("[GameCommand] • -> " + gameRequest.getReceiver().getName() + " (" + gameRequest.getGameMode().getName() + ")");
                        }
                    }
                    if (!receivedGameRequests.isEmpty()) {
                        client.sendMessage("[GameCommand] Received game requests:");
                        for (final GameRequest gameRequest : receivedGameRequests) {
                            client.sendMessage("[GameCommand] • <- " + gameRequest.getSender().getName() + " (" + gameRequest.getGameMode().getName() + ")");
                        }
                    }
                } else {
                    client.sendMessage("[GameCommand] No game requests found!");
                }
            } else if (arguments[0].equalsIgnoreCase("modes")) {
                client.sendMessage("[GameCommand] Available game modes:");
                for (final Map.Entry<Integer, GameMode> entry : this.gameModes.entrySet()) {
                    client.sendMessage("[GameCommand] " + entry.getKey() + ". " + entry.getValue().getName());
                }
            } else {
                client.sendMessage("[GameCommand] Use '/game help' for more information.");
            }
        } else if (arguments.length == 2) {
            if (arguments[0].equalsIgnoreCase("cancel")) {
                final String clientName = arguments[1];
                if (!clientName.isBlank()) {
                    if (!client.getName().equalsIgnoreCase(clientName)) {
                        if (server.containsClient(clientName)) {
                            final ServerClientThread receiverClient = server.getClient(clientName);
                            if (server.getGameManager().isSendedGameRequestExistent(client, receiverClient)) {
                                server.getGameManager().cancelGameRequest(client, receiverClient, false);
                            } else {
                                client.sendMessage("[GameCommand] You have not sent a game request to '" + receiverClient.getName() + "'!");
                            }
                        } else {
                            client.sendMessage("[GameCommand] The specified client does not exist!");
                        }
                    } else {
                        client.sendMessage("[GameCommand] You cannot cancel a game request from yourself!");
                    }
                } else {
                    client.sendMessage("[GameCommand] Usage: '/game cancel <clientName>'");
                }
            } else if (arguments[0].equalsIgnoreCase("accept")) {
                final String clientName = arguments[1];
                if (!clientName.isBlank()) {
                    if (!client.getName().equalsIgnoreCase(clientName)) {
                        if (server.containsClient(clientName)) {
                            final ServerClientThread senderClient = server.getClient(clientName);
                            if (server.getGameManager().isReceivedGameRequestExistent(client, senderClient)) {
                                server.getGameManager().acceptGameRequest(client, senderClient);
                            } else {
                                client.sendMessage("[GameCommand] You have not received a game request from '" + senderClient.getName() + "'!");
                            }
                        } else {
                            client.sendMessage("[GameCommand] The specified client does not exist!");
                        }
                    } else {
                        client.sendMessage("[GameCommand] You cannot accept a game request from yourself!");
                    }
                } else {
                    client.sendMessage("[GameCommand] Usage: '/game cancel <clientName>'");
                }
            } else if (arguments[0].equalsIgnoreCase("deny")) {
                final String clientName = arguments[1];
                if (!clientName.isBlank()) {
                    if (!client.getName().equalsIgnoreCase(clientName)) {
                        if (server.containsClient(clientName)) {
                            final ServerClientThread senderClient = server.getClient(clientName);
                            if (server.getGameManager().isReceivedGameRequestExistent(client, senderClient)) {
                                server.getGameManager().denyGameRequest(client, senderClient, false);
                            } else {
                                client.sendMessage("[GameCommand] You have not received a game request from '" + senderClient.getName() + "'!");
                            }
                        } else {
                            client.sendMessage("[GameCommand] The specified client does not exist!");
                        }
                    } else {
                        client.sendMessage("[GameCommand] You cannot deny a game request from yourself!");
                    }
                } else {
                    client.sendMessage("[GameCommand] Usage: '/game cancel <clientName>'");
                }
            } else if (arguments[0].equalsIgnoreCase("requests")) {
                final String type = arguments[1];
                if (!type.isBlank()) {
                    if (type.equalsIgnoreCase("SENDED")) {
                        final List<GameRequest> sendedGameRequests = server.getGameManager().getSendedGameRequests(client);
                        if (!sendedGameRequests.isEmpty()) {
                            client.sendMessage("[GameCommand] Sent game requests:");
                            for (final GameRequest gameRequest : sendedGameRequests) {
                                client.sendMessage("[GameCommand] • -> " + gameRequest.getReceiver().getName() + " (" + gameRequest.getGameMode().getName() + ")");
                            }
                        } else {
                            client.sendMessage("[GameCommand] No sent game requests found!");
                        }
                    } else if (type.equalsIgnoreCase("RECEIVED")) {
                        final List<GameRequest> receivedGameRequests = server.getGameManager().getReceivedGameRequests(client);
                        if (!receivedGameRequests.isEmpty()) {
                            client.sendMessage("[GameCommand] Received game requests:");
                            for (final GameRequest gameRequest : receivedGameRequests) {
                                client.sendMessage("[GameCommand] • <- " + gameRequest.getSender().getName() + " (" + gameRequest.getGameMode().getName() + ")");
                            }
                        } else {
                            client.sendMessage("[GameCommand] No received game requests found!");
                        }
                    } else {
                        client.sendMessage("[GameCommand] Usage: '/game requests (<SENDED ? RECEIVED>)'");
                    }
                } else {
                    client.sendMessage("[GameCommand] Usage: '/game requests (<SENDED ? RECEIVED>)'");
                }
            } else {
                client.sendMessage("[GameCommand] Use '/game help' for more information.");
            }
        } else if (arguments.length == 3) {
            if (arguments[0].equalsIgnoreCase("invite")) {
                final String rawGameMode = arguments[1];
                if (!rawGameMode.isBlank()) {
                    final GameMode resolvedGameMode = this.resolveGameMode(rawGameMode);
                    if (resolvedGameMode != null) {
                        final String clientName = arguments[2];
                        if (!clientName.isBlank()) {
                            if (!client.getName().equalsIgnoreCase(clientName)) {
                                if (server.containsClient(clientName)) {
                                    final ServerClientThread receiverClient = server.getClient(clientName);
                                    if (!receiverClient.getRoomName().equalsIgnoreCase("GLOBAL")) {
                                        final Room receiverRoom = server.getRoomManager().getRoom(receiverClient.getRoomName());
                                        if (receiverRoom != null) {
                                            if (receiverRoom.isGame()) {
                                                client.sendMessage("[GameCommand] The specified client is currently in a game!");
                                                return;
                                            }
                                        }
                                    }
                                    if (!server.getGameManager().isSendedGameRequestExistent(client, receiverClient)) {
                                        server.getGameManager().startGameRequest(resolvedGameMode, client, receiverClient);
                                    } else {
                                        client.sendMessage("[GameCommand] You have already sent a game request to '" + receiverClient.getName() + "'!");
                                    }
                                } else {
                                    client.sendMessage("[GameCommand] The specified client does not exist!");
                                }
                            } else {
                                client.sendMessage("[GameCommand] You cannot invite yourself to a game!");
                            }
                        } else {
                            client.sendMessage("[GameCommand] Usage: '/game invite <gameMode> <clientName>'");
                        }
                    } else {
                        client.sendMessage("[GameCommand] The specified game mode does not exist!");
                    }
                } else {
                    client.sendMessage("[GameCommand] Usage: '/game invite <gameMode> <clientName>'");
                }
            } else {
                client.sendMessage("[GameCommand] Use '/game help' for more information.");
            }
        } else {
            client.sendMessage("[GameCommand] Use '/game help' for more information.");
        }
    }

    private GameMode resolveGameMode(final String rawGameMode) {
        if (rawGameMode != null && !rawGameMode.isBlank()) {
            try {
                final int gameModeIndex = Integer.parseInt(rawGameMode);
                if (gameModeIndex >= 1 && gameModeIndex <= GameMode.values().length) {
                    return this.gameModes.get(gameModeIndex);
                }
            } catch (final NumberFormatException exception) {
                for (final GameMode gameMode : GameMode.values()) {
                    if (gameMode.getName().equalsIgnoreCase(rawGameMode)) {
                        return gameMode;
                    }
                }
            }
        }
        return null;
    }
}
