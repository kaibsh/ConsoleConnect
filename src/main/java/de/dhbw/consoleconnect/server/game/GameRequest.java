package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.ServerClient;

public final class GameRequest {

    private final GameMode gameMode;
    private final ServerClient sender;
    private final ServerClient receiver;

    GameRequest(final GameMode gameMode, final ServerClient sender, final ServerClient receiver) {
        this.gameMode = gameMode;
        this.sender = sender;
        this.receiver = receiver;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public ServerClient getSender() {
        return this.sender;
    }

    public ServerClient getReceiver() {
        return this.receiver;
    }
}
