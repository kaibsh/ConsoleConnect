package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.game.registry.TicTacToeGame;

public class GameRequest {

    private final GameMode gameMode;
    private final ServerClientThread sender;
    private final ServerClientThread receiver;

    protected GameRequest(final GameMode gameMode, final ServerClientThread sender, final ServerClientThread receiver) {
        this.gameMode = gameMode;
        this.sender = sender;
        this.receiver = receiver;
    }

    protected Game resolve() {
        final Game game;
        if (this.gameMode.equals(GameMode.TIC_TAC_TOE)) {
            game = new TicTacToeGame();
        } else {
            throw new IllegalArgumentException("Unsupported game mode: " + this.gameMode);
        }
        return game;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public ServerClientThread getSender() {
        return this.sender;
    }

    public ServerClientThread getReceiver() {
        return this.receiver;
    }
}
