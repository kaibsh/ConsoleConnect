package de.dhbw.consoleconnect.server.game.history;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.game.GameMode;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GameHistoryBuilder {

    private final Map<Account, Boolean> players = new HashMap<>();
    private UUID id;
    private Instant startTime;
    private Instant endTime;
    private GameMode gameMode;
    private boolean draw;

    public GameHistoryBuilder setId(final UUID id) {
        this.id = id;
        return this;
    }

    public GameHistoryBuilder setStartTime(final Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public GameHistoryBuilder setEndTime(final Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public GameHistoryBuilder setGameMode(final GameMode gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    public GameHistoryBuilder setDraw(final boolean draw) {
        this.draw = draw;
        return this;
    }

    public GameHistoryBuilder addPlayers(final Map<Account, Boolean> players) {
        this.players.putAll(players);
        return this;
    }

    public GameHistory build() {
        final GameHistory gameHistory = new GameHistory();
        gameHistory.setId(this.id);
        gameHistory.setStartTime(this.startTime);
        gameHistory.setEndTime(this.endTime);
        gameHistory.setGameMode(this.gameMode);
        gameHistory.setDraw(this.draw);
        gameHistory.getPlayers().putAll(this.players);
        return gameHistory;
    }
}
