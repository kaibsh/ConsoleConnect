package de.dhbw.consoleconnect.server.game;

public enum GameMode {

    TIC_TAC_TOE("Tic-Tac-Toe");

    private final String name;

    GameMode(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
