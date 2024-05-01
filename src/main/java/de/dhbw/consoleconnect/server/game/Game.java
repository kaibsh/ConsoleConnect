package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.room.Room;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public abstract class Game {

    private final UUID id;
    private final GameMode gameMode;
    private final Room room;
    private Instant startTime;
    private Instant endTime;
    private ServerClientThread winner;
    private boolean running = false;

    protected Game(final GameMode gameMode) {
        this.id = UUID.randomUUID();
        this.gameMode = gameMode;
        this.room = new Room(this.gameMode.getName() + "-" + this.id, this.gameMode.getName(), true);
    }

    protected final void start() {
        if (!this.running) {
            this.startTime = Instant.now();
            this.clear();
            this.initialize();
            this.running = true;
        }
    }

    protected final void stop() {
        if (this.running) {
            this.running = false;
            this.endTime = Instant.now();
            this.broadcast("The game took " + this.getDuration().getSeconds() + " seconds to complete!");
        }
    }

    protected final void broadcast(final String message, final ServerClientThread... clients) {
        if (message != null && !message.isBlank()) {
            if (clients != null && clients.length > 0) {
                for (final ServerClientThread client : clients) {
                    client.sendMessage(message);
                }
            } else {
                for (final ServerClientThread client : this.getRoom().getClients()) {
                    client.sendMessage(message);
                }
            }
        }
    }

    protected final void clear(final ServerClientThread... clients) {
        if (clients != null) {
            this.broadcast("[HOOK] CLEAR", clients);
        }
    }

    protected abstract void initialize();

    protected abstract void handleInput(final Server server, final ServerClientThread client, final String input);

    public UUID getId() {
        return this.id;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public final Room getRoom() {
        return this.room;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Duration getDuration() {
        if (this.startTime != null && this.endTime != null) {
            return Duration.between(this.startTime, this.endTime);
        }
        return Duration.ZERO;
    }

    public ServerClientThread getWinner() {
        return this.winner;
    }

    protected void setWinner(final ServerClientThread winner) {
        this.winner = winner;
    }

    public final boolean isRunning() {
        return this.running;
    }
}
