package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.database.h2.H2Database;
import de.dhbw.consoleconnect.server.database.h2.registry.GameHistoryRepositoryH2Database;
import de.dhbw.consoleconnect.server.database.repositories.GameHistoryRepository;
import de.dhbw.consoleconnect.server.room.Room;

import java.util.*;

public class GameManager {

    private final Server server;
    private final GameHistoryRepository gameHistoryRepository;
    private final List<Game> games = new LinkedList<>();
    private final List<GameRequest> gameRequests = new LinkedList<>();

    public GameManager(final Server server) {
        this.server = server;
        this.gameHistoryRepository = new GameHistoryRepositoryH2Database();
        this.server.getDatabaseService().registerDatabase((H2Database) this.gameHistoryRepository);
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

            //START: GameHistory
            final GameHistory gameHistory = new GameHistory();
            gameHistory.setId(game.getId());
            gameHistory.setGameMode(game.getGameMode());
            gameHistory.setStartTime(game.getStartTime());
            gameHistory.setEndTime(game.getEndTime());
            gameHistory.setDraw(game.getWinner() == null);
            for (final ServerClient client : game.getRoom().getClients()) {
                final Account account = this.server.getAccountManager().getAccountByName(client.getName());
                if (account != null) {
                    gameHistory.getPlayers().put(account, (game.getWinner() != null && game.getWinner() == client));
                }
            }
            this.gameHistoryRepository.saveGameHistory(gameHistory);
            //END: GameHistory

            this.games.remove(game);
            this.server.getRoomManager().removeRoom(game.getRoom());
            System.out.println("[GAME] Game '" + game.getRoom().getName() + "' has been stopped.");
        }
    }

    public void startGameRequest(final GameMode gameMode, final ServerClient senderClient, final ServerClient receiverClient) {
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
                senderClient.sendMessage("[GameManager] Successfully sent a game request to '" + receiverClient.getName() + "'!");
                receiverClient.sendMessage("[GameManager] You have received a game request from '" + senderClient.getName() + "'!");
                receiverClient.sendMessage("[GameManager] - Use '/game accept " + senderClient.getName() + "' to accept the game request.");
                receiverClient.sendMessage("[GameManager] - Use '/game deny " + senderClient.getName() + "' to deny the game request.");
                System.out.println("[GAME] Request from '" + senderClient.getName() + "' to '" + receiverClient.getName() + "' has been started.");
            }
        }
    }

    public void cancelGameRequest(final ServerClient senderClient, final ServerClient receiverClient, final boolean silent) {
        if (senderClient != null && receiverClient != null && senderClient != receiverClient) {
            final GameRequest gameRequest = this.getGameRequest(senderClient, receiverClient);
            if (gameRequest != null) {
                this.gameRequests.remove(gameRequest);
                if (!silent) {
                    senderClient.sendMessage("[GameManager] Successfully canceled the game request to '" + receiverClient.getName() + "'!");
                }
                receiverClient.sendMessage("[GameManager] The game request from '" + senderClient.getName() + "' has been canceled!");
                System.out.println("[GAME] Request from '" + senderClient.getName() + "' to '" + receiverClient.getName() + "' has been canceled.");
            }
        }
    }

    public void acceptGameRequest(final ServerClient receiverClient, final ServerClient senderClient) {
        if (receiverClient != null && senderClient != null && receiverClient != senderClient) {
            final GameRequest gameRequest = this.getGameRequest(senderClient, receiverClient);
            if (gameRequest != null) {
                receiverClient.sendMessage("[GameManager] The game request from '" + senderClient.getName() + "' has been accepted!");
                senderClient.sendMessage("[GameManager] The game request to '" + receiverClient.getName() + "' has been accepted!");
                System.out.println("[GAME] Request from '" + senderClient.getName() + "' to '" + receiverClient.getName() + "' has been accepted.");
                this.startGame(gameRequest);
            }
        }
    }

    public void denyGameRequest(final ServerClient receiverClient, final ServerClient senderClient, final boolean silent) {
        if (receiverClient != null && senderClient != null && receiverClient != senderClient) {
            final GameRequest gameRequest = this.getGameRequest(senderClient, receiverClient);
            if (gameRequest != null) {
                this.gameRequests.remove(gameRequest);
                if (!silent) {
                    receiverClient.sendMessage("[GameManager] The game request from '" + senderClient.getName() + "' has been denied!");
                }
                senderClient.sendMessage("[GameManager] The game request to '" + receiverClient.getName() + "' has been denied!");
                System.out.println("[GAME] Request from '" + senderClient.getName() + "' to '" + receiverClient.getName() + "' has been declined.");
            }
        }
    }

    public void removeAllGameRequests(final ServerClient client) {
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

    public void handleGameInput(final Game game, final ServerClient client, final String input) {
        if (game != null && client != null && input != null && !input.isBlank() && this.games.contains(game) && game.getRoom().getClients().contains(client)) {
            if (game.isRunning()) {
                if (!input.equalsIgnoreCase("exit") && !input.equalsIgnoreCase("leave") && !input.equalsIgnoreCase("stop")) {
                    game.handleInput(this.server, client, input);
                } else {
                    this.server.getRoomManager().leaveRoom(game.getRoom(), false, client);
                }
            }
        }
    }

    public List<GameHistory> getGameHistories(final Account account) {
        final List<GameHistory> gameHistories = new ArrayList<>();
        if (account != null) {
            final List<UUID> gameHistoryIDs = this.gameHistoryRepository.getGameHistoryIDs(account);
            for (final UUID gameId : gameHistoryIDs) {
                final GameHistory gameHistory = this.gameHistoryRepository.getGameHistory(gameId);
                if (gameHistory != null) {
                    final Map<Integer, Boolean> players = this.gameHistoryRepository.getGameHistoryPlayers(gameId);
                    for (final Map.Entry<Integer, Boolean> player : players.entrySet()) {
                        final Account playerAccount = this.server.getAccountManager().getAccountById(player.getKey());
                        if (playerAccount != null) {
                            gameHistory.getPlayers().put(playerAccount, player.getValue());
                        }
                    }
                    gameHistories.add(gameHistory);
                }
            }

        }
        return gameHistories;
    }

    public boolean isInGame(final ServerClient client) {
        for (final Game game : this.games) {
            if (game.getRoom().getClients().contains(client)) {
                return true;
            }
        }
        return false;
    }

    public Game getGame(final ServerClient client) {
        for (final Game game : this.games) {
            if (game.getRoom().getClients().contains(client)) {
                return game;
            }
        }
        return null;
    }

    public GameRequest getGameRequest(final ServerClient senderClient, final ServerClient receiverClient) {
        if (senderClient != null && receiverClient != null && senderClient != receiverClient) {
            for (final GameRequest gameRequest : this.gameRequests) {
                if (gameRequest.getSender() == senderClient && gameRequest.getReceiver() == receiverClient) {
                    return gameRequest;
                }
            }
        }
        return null;
    }

    public List<GameRequest> getAllGameRequests(final ServerClient client) {
        final List<GameRequest> allGameRequests = new LinkedList<>();
        for (final GameRequest gameRequest : this.gameRequests) {
            if (gameRequest.getSender() == client || gameRequest.getReceiver() == client) {
                allGameRequests.add(gameRequest);
            }
        }
        return allGameRequests;
    }

    public List<GameRequest> getSendedGameRequests(final ServerClient senderClient) {
        final List<GameRequest> sendedGameRequests = new LinkedList<>();
        for (final GameRequest gameRequest : this.gameRequests) {
            if (gameRequest.getSender() == senderClient) {
                sendedGameRequests.add(gameRequest);
            }
        }
        return sendedGameRequests;
    }


    public boolean isSendedGameRequestExistent(final ServerClient senderClient, final ServerClient receiverClient) {
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

    public List<GameRequest> getReceivedGameRequests(final ServerClient receiverClient) {
        final List<GameRequest> receivedGameRequests = new LinkedList<>();
        for (final GameRequest gameRequest : this.gameRequests) {
            if (gameRequest.getReceiver() == receiverClient) {
                receivedGameRequests.add(gameRequest);
            }
        }
        return receivedGameRequests;
    }

    public boolean isReceivedGameRequestExistent(final ServerClient receiverClient, final ServerClient senderClient) {
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
