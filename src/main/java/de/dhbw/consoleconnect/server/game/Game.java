package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.room.Room;

import java.util.UUID;

public abstract class Game {

    private final GameMode gameMode;
    private final Room room;
    private boolean running = false;

    protected Game(final GameMode gameMode) {
        this.gameMode = gameMode;
        this.room = new Room(this.gameMode.getName() + "-" + UUID.randomUUID(), this.gameMode.getName(), true);
    }

    protected final void start() {
        if (!this.running) {
            this.running = true;
            this.handleStart();
        }
    }

    protected final void stop() {
        if (this.running) {
            this.running = false;
            this.handleStop();
        }
    }

    protected abstract void handleStart();

    protected abstract void handleStop();

    protected abstract void handleInput(final ServerClientThread client, final String input);

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public final Room getRoom() {
        return this.room;
    }

    public final boolean isRunning() {
        return this.running;
    }
}
