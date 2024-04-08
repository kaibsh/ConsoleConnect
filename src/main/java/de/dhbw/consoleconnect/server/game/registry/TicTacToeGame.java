package de.dhbw.consoleconnect.server.game.registry;

import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.game.Game;
import de.dhbw.consoleconnect.server.game.GameMode;

public class TicTacToeGame extends Game {

    public TicTacToeGame() {
        super(GameMode.TIC_TAC_TOE);
    }

    @Override
    protected void handleStart() {

    }

    @Override
    protected void handleStop() {

    }

    @Override
    protected void handleInput(final ServerClientThread client, final String input) {

    }
}
