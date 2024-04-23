package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.room.Room;

import java.util.LinkedList;
import java.util.List;

public class GameManager {

    private final Server server;
    private final List<Game> games = new LinkedList<>();
    private final List<GameRequest> gameRequests = new LinkedList<>();

    public GameManager(final Server server) {
        this.server = server;
    }

    public void startGame(final GameRequest gameRequest) {
        if (gameRequest != null && this.gameRequests.contains(gameRequest)) {
            this.gameRequests.remove(gameRequest);
            this.removeAllGameRequests(gameRequest.getSender());
            this.removeAllGameRequests(gameRequest.getReceiver());
            final Game game = gameRequest.resolve();
            this.server.getRoomManager().addRoom(game.getRoom(), gameRequest.getSender(), gameRequest.getReceiver());
            this.games.add(game);
            game.start();
            System.out.println("[GAME] Game '" + game.getRoom().getName() + "' has been started.");
        }
    }

    public void stopGame(final Game game) {
        if (game != null && this.games.contains(game)) {
            game.stop();
            this.games.remove(game);
            this.server.getRoomManager().removeRoom(game.getRoom());
            System.out.println("[GAME] Game '" + game.getRoom().getName() + "' has been stopped.");
        }
    }

    public void startGameRequest(final GameMode gameMode, final ServerClientThread senderClient, final ServerClientThread receiverClient) {
        if (gameMode != null && senderClient != null && receiverClient != null && senderClient != receiverClient) {
            if (!senderClient.getRoomName().equalsIgnoreCase("GLOBAL") && !receiverClient.getRoomName().equalsIgnoreCase("GLOBAL")) {
                final Room senderRoom = this.server.getRoomManager().getRoom(senderClient.getRoomName());
                final Room receiverRoom = this.server.getRoomManager().getRoom(receiverClient.getRoomName());
                if (senderRoom != null && receiverRoom != null) {
                    if (senderRoom.isGame() || receiverRoom.isGame()) {
                        return;
                    }
                }
            }
            if (!this.isSendedGameRequestExistent(senderClient, receiverClient)) {
                final GameRequest gameRequest = new GameRequest(gameMode, senderClient, receiverClient);
                this.gameRequests.add(gameRequest);
                senderClient.sendMessage("[GameManager] Successfully sent a game request to '" + receiverClient.getClientName() + "'!");
                receiverClient.sendMessage("[GameManager] You have received a game request from '" + senderClient.getClientName() + "'!");
                receiverClient.sendMessage("[GameManager] - Use '/game accept " + senderClient.getClientName() + "' to accept the game request.");
                receiverClient.sendMessage("[GameManager] - Use '/game deny " + senderClient.getClientName() + "' to deny the game request.");
                System.out.println("[GAME] Request from '" + senderClient.getClientName() + "' to '" + receiverClient.getClientName() + "' has been started.");
            }
        }
    }

    public void cancelGameRequest(final ServerClientThread senderClient, final ServerClientThread receiverClient, final boolean silent) {
        if (senderClient != null && receiverClient != null && senderClient != receiverClient) {
            final GameRequest gameRequest = this.getGameRequest(senderClient, receiverClient);
            if (gameRequest != null) {
                this.gameRequests.remove(gameRequest);
                if (!silent) {
                    senderClient.sendMessage("[GameManager] Successfully canceled the game request to '" + receiverClient.getClientName() + "'!");
                }
                receiverClient.sendMessage("[GameManager] The game request from '" + senderClient.getClientName() + "' has been canceled!");
                System.out.println("[GAME] Request from '" + senderClient.getClientName() + "' to '" + receiverClient.getClientName() + "' has been canceled.");
            }
        }
    }

    public void acceptGameRequest(final ServerClientThread receiverClient, final ServerClientThread senderClient) {
        if (receiverClient != null && senderClient != null && receiverClient != senderClient) {
            final GameRequest gameRequest = this.getGameRequest(senderClient, receiverClient);
            if (gameRequest != null) {
                receiverClient.sendMessage("[GameManager] The game request from '" + senderClient.getClientName() + "' has been accepted!");
                senderClient.sendMessage("[GameManager] The game request to '" + receiverClient.getClientName() + "' has been accepted!");
                System.out.println("[GAME] Request from '" + senderClient.getClientName() + "' to '" + receiverClient.getClientName() + "' has been accepted.");
                this.startGame(gameRequest);
            }
        }
    }

    public void denyGameRequest(final ServerClientThread receiverClient, final ServerClientThread senderClient, final boolean silent) {
        if (receiverClient != null && senderClient != null && receiverClient != senderClient) {
            final GameRequest gameRequest = this.getGameRequest(senderClient, receiverClient);
            if (gameRequest != null) {
                this.gameRequests.remove(gameRequest);
                if (!silent) {
                    receiverClient.sendMessage("[GameManager] The game request from '" + senderClient.getClientName() + "' has been denied!");
                }
                senderClient.sendMessage("[GameManager] The game request to '" + receiverClient.getClientName() + "' has been denied!");
                System.out.println("[GAME] Request from '" + senderClient.getClientName() + "' to '" + receiverClient.getClientName() + "' has been declined.");
            }
        }
    }

    public void removeAllGameRequests(final ServerClientThread client) {
        if (client != null) {
            final List<GameRequest> overallGameRequests = this.getAllGameRequests(client);
            for (final GameRequest gameRequest : overallGameRequests) {
                if (gameRequest.getSender() == client) {
                    this.cancelGameRequest(gameRequest.getSender(), gameRequest.getReceiver(), true);
                } else if (gameRequest.getReceiver() == client) {
                    this.denyGameRequest(gameRequest.getReceiver(), gameRequest.getSender(), true);
                }
            }
        }
    }

    public void handleGameInput(final Game game, final ServerClientThread client, final String input) {
        if (game != null && client != null && input != null && !input.isBlank() && this.games.contains(game) && game.getRoom().getClients().contains(client)) {
            if (game.isRunning()) {
                if (!input.equalsIgnoreCase("exit") && !input.equalsIgnoreCase("leave") && !input.equalsIgnoreCase("stop")) {
                    game.handleInput(client, input);
                } else {
                    this.server.getRoomManager().leaveRoom(game.getRoom(), false, client);
                }
            } else {
                System.out.println("DEBUG: Game is not running!");
                this.stopGame(game);
            }
        }
    }

    public Game getGame(final ServerClientThread client) {
        for (final Game game : this.games) {
            if (game.getRoom().getClients().contains(client)) {
                return game;
            }
        }
        return null;
    }

    public GameRequest getGameRequest(final ServerClientThread senderClient, final ServerClientThread receiverClient) {
        if (senderClient != null && receiverClient != null && senderClient != receiverClient) {
            for (final GameRequest gameRequest : this.gameRequests) {
                if (gameRequest.getSender() == senderClient && gameRequest.getReceiver() == receiverClient) {
                    return gameRequest;
                }
            }
        }
        return null;
    }

    public List<GameRequest> getAllGameRequests(final ServerClientThread client) {
        final List<GameRequest> allGameRequests = new LinkedList<>();
        for (final GameRequest gameRequest : this.gameRequests) {
            if (gameRequest.getSender() == client || gameRequest.getReceiver() == client) {
                allGameRequests.add(gameRequest);
            }
        }
        return allGameRequests;
    }

    public List<GameRequest> getSendedGameRequests(final ServerClientThread senderClient) {
        final List<GameRequest> sendedGameRequests = new LinkedList<>();
        for (final GameRequest gameRequest : this.gameRequests) {
            if (gameRequest.getSender() == senderClient) {
                sendedGameRequests.add(gameRequest);
            }
        }
        return sendedGameRequests;
    }


    public boolean isSendedGameRequestExistent(final ServerClientThread senderClient, final ServerClientThread receiverClient) {
        if (senderClient != null && receiverClient != null && senderClient != receiverClient) {
            final List<GameRequest> sendedGameRequests = this.getSendedGameRequests(senderClient);
            for (final GameRequest gameRequest : sendedGameRequests) {
                if (gameRequest.getReceiver() == receiverClient) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<GameRequest> getReceivedGameRequests(final ServerClientThread receiverClient) {
        final List<GameRequest> receivedGameRequests = new LinkedList<>();
        for (final GameRequest gameRequest : this.gameRequests) {
            if (gameRequest.getReceiver() == receiverClient) {
                receivedGameRequests.add(gameRequest);
            }
        }
        return receivedGameRequests;
    }

    public boolean isReceivedGameRequestExistent(final ServerClientThread receiverClient, final ServerClientThread senderClient) {
        if (receiverClient != null && senderClient != null && receiverClient != senderClient) {
            final List<GameRequest> receivedGameRequests = this.getReceivedGameRequests(receiverClient);
            for (final GameRequest gameRequest : receivedGameRequests) {
                if (gameRequest.getSender() == senderClient) {
                    return true;
                }
            }
        }
        return false;
    }
}
