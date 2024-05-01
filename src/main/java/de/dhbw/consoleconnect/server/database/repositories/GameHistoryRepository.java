package de.dhbw.consoleconnect.server.database.repositories;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.game.GameHistory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GameHistoryRepository {

    GameHistory getGameHistory(final UUID gameId);

    List<UUID> getGameHistoryIDs(final Account account);

    Map<Integer, Boolean> getGameHistoryPlayers(final UUID gameId);

    void saveGameHistory(final GameHistory gameHistory);
}
