package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.game.registry.RockPaperScissorGame;
import de.dhbw.consoleconnect.server.game.registry.TicTacToeGame;

public class GameRequest {

    private final GameMode gameMode;
    private final ServerClient sender;
    private final ServerClient receiver;

    protected GameRequest(final GameMode gameMode, final ServerClient sender, final ServerClient receiver) {
        this.gameMode = gameMode;
        this.sender = sender;
        this.receiver = receiver;
    }

    protected Game resolve() {
        final Game game;
        if (this.gameMode.equals(GameMode.ROCK_PAPER_SCISSOR)) {
            game = new RockPaperScissorGame();
        } else if (this.gameMode.equals(GameMode.TIC_TAC_TOE)) {
            game = new TicTacToeGame();
        } else {
            throw new IllegalArgumentException("Unsupported game mode: " + this.gameMode);
        }
        return game;
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
