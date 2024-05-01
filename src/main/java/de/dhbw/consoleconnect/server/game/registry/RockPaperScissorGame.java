package de.dhbw.consoleconnect.server.game.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.game.Game;
import de.dhbw.consoleconnect.server.game.GameMode;

import java.util.Map;

public class RockPaperScissorGame extends Game {

    private Map.Entry<ServerClient, Selection> firstPlayerSelection;
    private Map.Entry<ServerClient, Selection> secondPlayerSelection;

    public RockPaperScissorGame() {
        super(GameMode.ROCK_PAPER_SCISSOR);
    }

    @Override
    protected void initialize() {
        this.broadcast("**** Welcome to RockPaperScissor ****");
        this.broadcast("Please enter your selection: Rock, Paper or Scissor");
    }

    @Override
    protected void handleInput(final Server server, final ServerClient client, final String input) {
        if (client != null && input != null && !input.isBlank()) {
            if ((this.firstPlayerSelection != null && this.firstPlayerSelection.getKey() == client) || (this.secondPlayerSelection != null && this.secondPlayerSelection.getKey() == client)) {
                client.sendMessage("You already made your selection!");
                return;
            }
            final Selection selection = Selection.fromString(input);
            if (selection != null) {
                final Map.Entry<ServerClient, Selection> playerSelection = Map.entry(client, selection);
                if (this.firstPlayerSelection == null) {
                    this.firstPlayerSelection = playerSelection;
                    client.sendMessage("You selected: " + selection.name());
                    this.broadcast("Player 1 made a selection!");
                } else {
                    this.secondPlayerSelection = playerSelection;
                    client.sendMessage("You selected: " + selection.name());
                    this.broadcast("Player 2 made a selection!");
                    this.checkWinner();
                    server.getGameManager().stopGame(this);
                }
            } else {
                client.sendMessage("Invalid selection! Please enter: Rock, Paper or Scissor");
            }
        }
    }

    private void checkWinner() {
        final Selection firstPlayerSelection = this.firstPlayerSelection.getValue();
        final Selection secondPlayerSelection = this.secondPlayerSelection.getValue();
        if (firstPlayerSelection == secondPlayerSelection) {
            this.broadcast("The game ended in a draw!");
        } else if (firstPlayerSelection == Selection.BLACK_HOLE ||
                firstPlayerSelection == Selection.ROCK && secondPlayerSelection == Selection.SCISSOR ||
                firstPlayerSelection == Selection.PAPER && secondPlayerSelection == Selection.ROCK ||
                firstPlayerSelection == Selection.SCISSOR && secondPlayerSelection == Selection.PAPER) {
            this.setWinner(this.firstPlayerSelection.getKey());
            this.broadcast(firstPlayerSelection.name() + " beats " + secondPlayerSelection.name() + "!");
            this.broadcast("The Player '" + this.firstPlayerSelection.getKey().getName() + "' has won the game!");
        } else {
            this.setWinner(this.secondPlayerSelection.getKey());
            this.broadcast(secondPlayerSelection.name() + " beats " + firstPlayerSelection.name() + "!");
            this.broadcast("The Player '" + this.secondPlayerSelection.getKey().getName() + "' has won the game!");
        }
    }

    private enum Selection {
        ROCK, PAPER, SCISSOR, BLACK_HOLE;

        public static Selection fromString(final String input) {
            return switch (input.toLowerCase()) {
                case "rock" -> ROCK;
                case "paper" -> PAPER;
                case "scissor" -> SCISSOR;
                case "black-hole" -> BLACK_HOLE;
                default -> null;
            };
        }
    }
}
