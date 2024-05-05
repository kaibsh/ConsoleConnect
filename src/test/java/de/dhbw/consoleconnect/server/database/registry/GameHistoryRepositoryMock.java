package de.dhbw.consoleconnect.server.database.registry;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.database.repositories.GameHistoryRepository;
import de.dhbw.consoleconnect.server.game.GameHistory;

import java.util.*;

public final class GameHistoryRepositoryMock implements GameHistoryRepository<List<GameHistory>> {

    private List<GameHistory> gameHistories;

    @Override
    public void initialize(final List<GameHistory> gameHistories) {
        this.gameHistories = gameHistories;
    }

    @Override
    public GameHistory getGameHistory(final UUID gameId) {
        if (gameId != null) {
            for (final GameHistory gameHistory : this.gameHistories) {
                if (gameHistory.getId().equals(gameId)) {
                    return gameHistory;
                }
            }
        }
        return null;
    }

    @Override
    public List<UUID> getGameHistoryIDs(final Account account) {
        final List<UUID> gameHistoryIDs = new ArrayList<>();
        if (account != null) {
            for (final GameHistory gameHistory : this.gameHistories) {
                if (gameHistory.getPlayers().containsKey(account)) {
                    gameHistoryIDs.add(gameHistory.getId());
                }
            }
        }
        return gameHistoryIDs;
    }

    @Override
    public Map<Integer, Boolean> getGameHistoryPlayers(final UUID gameId) {
        final Map<Integer, Boolean> gameHistoryPlayers = new HashMap<>();
        if (gameId != null) {
            for (final GameHistory gameHistory : this.gameHistories) {
                if (gameHistory.getId().equals(gameId)) {
                    for (final Map.Entry<Account, Boolean> entry : gameHistory.getPlayers().entrySet()) {
                        gameHistoryPlayers.put(entry.getKey().getId(), entry.getValue());
                    }
                }
            }
        }
        return gameHistoryPlayers;
    }

    @Override
    public void saveGameHistory(final GameHistory gameHistory) {
        if (gameHistory != null) {
            this.gameHistories.add(gameHistory);
        }
    }
}
