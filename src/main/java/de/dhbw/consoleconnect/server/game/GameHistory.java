package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.account.Account;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public final class GameHistory {

    private final Map<Account, Boolean> players = new HashMap<>();
    private UUID id;
    private Instant startTime;
    private Instant endTime;
    private GameMode gameMode;
    private boolean draw;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        if (id != null) {
            this.id = id;
        }
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final Instant startTime) {
        if (startTime != null) {
            this.startTime = startTime;
        }
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final Instant endTime) {
        if (endTime != null) {
            this.endTime = endTime;
        }
    }

    public Duration getDuration() {
        if (this.startTime != null && this.endTime != null) {
            return Duration.between(this.startTime, this.endTime);
        }
        return Duration.ZERO;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(final GameMode gameMode) {
        if (gameMode != null) {
            this.gameMode = gameMode;
        }
    }

    public boolean isDraw() {
        return this.draw;
    }

    public void setDraw(final boolean draw) {
        this.draw = draw;
    }

    public Map<Account, Boolean> getPlayers() {
        return this.players;
    }

    public Account getWinner() {
        for (final Map.Entry<Account, Boolean> entry : this.players.entrySet()) {
            if (entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<Account> getLosers() {
        final List<Account> losers = new ArrayList<>();
        for (final Map.Entry<Account, Boolean> entry : this.players.entrySet()) {
            if (!entry.getValue()) {
                losers.add(entry.getKey());
            }
        }
        return losers;
    }
}
