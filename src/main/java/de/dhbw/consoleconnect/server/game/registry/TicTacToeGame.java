package de.dhbw.consoleconnect.server.game.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.game.Game;
import de.dhbw.consoleconnect.server.game.GameMode;

import java.util.Arrays;

public class TicTacToeGame extends Game {

    private static final int[][] WIN_CHANCES = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
    private final char[] board = new char[9];
    private ServerClient crossPlayer;
    private ServerClient circlePlayer;
    private Symbol currentPlayer = Symbol.CROSS;

    public TicTacToeGame() {
        super(GameMode.TIC_TAC_TOE);
        Arrays.fill(this.board, ' ');
    }

    @Override
    protected void initialize() {
        if (this.getRoom().getClients().size() == 2) {
            this.renderBoard();
            this.broadcast("**** Welcome to TicTacToe ****");
            final int startingPlayer = (int) Math.round(Math.random());
            this.crossPlayer = this.getRoom().getClients().get(startingPlayer == 0 ? 0 : 1);
            this.crossPlayer.sendMessage("Your symbol is: " + Symbol.CROSS.getCharacter());
            this.circlePlayer = this.getRoom().getClients().get(startingPlayer == 0 ? 1 : 0);
            this.circlePlayer.sendMessage("Your symbol is: " + Symbol.CIRCLE.getCharacter());
            this.broadcast("The first move needs to be made by: " + this.currentPlayer.getCharacter());
        } else {
            throw new IllegalStateException("The Tic-Tac-Toe game mode needs exactly 2 players!");
        }
    }

    @Override
    protected void handleInput(final Server server, final ServerClient client, final String input) {
        if (client != null && input != null && !input.isBlank()) {
            if (client == this.crossPlayer || client == this.circlePlayer) {
                if (this.handleTurn(client, input)) {
                    this.renderBoard();
                    this.broadcast("Player '" + this.currentPlayer.getCharacter() + "' made a move.");
                    if (this.checkWinner()) {
                        this.setWinner(this.getCurrentClient());
                        this.broadcast("The Player '" + this.getCurrentClient().getName() + "' has won the game!");
                        server.getGameManager().stopGame(this);
                    } else if (this.checkDraw()) {
                        this.broadcast("The game ended in a draw!");
                        server.getGameManager().stopGame(this);
                    } else {
                        this.currentPlayer = this.currentPlayer.next();
                        this.getCurrentClient().sendMessage("It's your turn!");
                    }
                }
            }
        }
    }

    private boolean handleTurn(final ServerClient client, final String input) {
        if (client != null && input != null && !input.isBlank()) {
            if ((client == this.crossPlayer && this.currentPlayer == Symbol.CROSS) || (client == this.circlePlayer && this.currentPlayer == Symbol.CIRCLE)) {
                final int position = this.parseInput(input);
                if (position != -1 && position < this.board.length) {
                    if (this.board[position] != Symbol.CROSS.getCharacter() && this.board[position] != Symbol.CIRCLE.getCharacter()) {
                        this.board[position] = this.currentPlayer.getCharacter();
                        return true;
                    } else {
                        this.renderBoard(client);
                        client.sendMessage("This field is already taken!");
                    }
                } else {
                    this.renderBoard(client);
                    client.sendMessage("Invalid input! Please enter a number between 1 and 9.");
                }
            } else {
                this.renderBoard(client);
                client.sendMessage("It's not your turn!");
            }
        }
        return false;
    }

    private void renderBoard(final ServerClient... clients) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" --- --- ---\n");
        int counter = 0;
        for (final char character : this.board) {
            if (counter == 0) {
                stringBuilder.append("| ").append(character);
            } else if (counter == 1) {
                stringBuilder.append(" | ").append(character).append(" | ");
            } else if (counter == 2) {
                stringBuilder.append(character).append(" |").append("\n --- --- ---\n");
                counter = -1;
            }
            counter++;
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        this.clear(clients);
        this.broadcast(stringBuilder.toString(), clients);
    }

    private int parseInput(final String input) {
        try {
            final int position = Integer.parseInt(input);
            if (position >= 1 && position <= 9) {
                return position - 1;
            }
        } catch (final NumberFormatException exception) {
            return -1;
        }
        return -1;
    }

    private boolean checkWinner() {
        for (final int[] winChance : WIN_CHANCES) {
            int counter = 0;
            for (final int index : winChance) {
                if (this.board[index] == this.currentPlayer.getCharacter()) {
                    counter++;
                }
                if (counter == 3) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDraw() {
        int counter = 0;
        for (final char character : this.board) {
            if (character != ' ') {
                counter++;
            }
        }
        return counter == 9;
    }

    private ServerClient getCurrentClient() {
        if (this.currentPlayer == Symbol.CROSS) {
            return this.crossPlayer;
        }
        return this.circlePlayer;
    }

    private enum Symbol {

        CROSS('x'),
        CIRCLE('o');

        private final char character;

        Symbol(final char character) {
            this.character = character;
        }

        public Symbol next() {
            return this == CROSS ? CIRCLE : CROSS;
        }

        public char getCharacter() {
            return this.character;
        }
    }
}
