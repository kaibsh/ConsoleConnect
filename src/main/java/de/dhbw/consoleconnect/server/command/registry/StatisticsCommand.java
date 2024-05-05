package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.command.Command;
import de.dhbw.consoleconnect.server.game.GameMode;
import de.dhbw.consoleconnect.server.game.history.GameHistory;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StatisticsCommand extends Command {

    public StatisticsCommand() {
        super("statistics", "Shows your statistics.");
    }

    @Override
    protected void execute(final Server server, final ServerClient client, final String[] arguments) {
        if (arguments == null) {
            final Account account = server.getAccountManager().getAccountByName(client.getName());
            if (account != null) {
                final List<GameHistory> gameHistories = server.getGameManager().getGameHistories(account);
                if (!gameHistories.isEmpty()) {
                    final Map<GameMode, Integer> gamesPlayed = this.getGamesPlayed(gameHistories, account);
                    client.sendMessage("[StatisticsCommand] Wins: " + this.getWins(gameHistories, account));
                    client.sendMessage("[StatisticsCommand] Losses: " + this.getLosses(gameHistories, account));
                    client.sendMessage("[StatisticsCommand] Draws: " + this.getDraws(gameHistories, account));
                    client.sendMessage("[StatisticsCommand] Total play time: " + this.getTotalPlayTime(gameHistories, account).getSeconds() + " seconds");
                    client.sendMessage("[StatisticsCommand] Games played:");
                    for (final Map.Entry<GameMode, Integer> entry : gamesPlayed.entrySet()) {
                        client.sendMessage("[StatisticsCommand] - " + entry.getKey().getName() + ": " + entry.getValue());
                    }
                } else {
                    client.sendMessage("[StatisticsCommand] No game history found!");
                }
            } else {
                client.sendMessage("[StatisticsCommand] Account not found!");
            }
        } else {
            client.sendMessage("[StatisticsCommand] This command does not take any arguments.");
        }
    }

    public int getWins(final List<GameHistory> gameHistories, final Account account) {
        int counter = 0;
        for (final GameHistory gameHistory : gameHistories) {
            if (!gameHistory.isDraw()) {
                if (gameHistory.getWinner().getName().equalsIgnoreCase(account.getName())) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public int getLosses(final List<GameHistory> gameHistories, final Account account) {
        int counter = 0;
        for (final GameHistory gameHistory : gameHistories) {
            if (!gameHistory.isDraw()) {
                if (!gameHistory.getWinner().getName().equalsIgnoreCase(account.getName())) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public int getDraws(final List<GameHistory> gameHistories, final Account account) {
        int counter = 0;
        for (final GameHistory gameHistory : gameHistories) {
            if (gameHistory.isDraw()) {
                counter++;
            }
        }
        return counter;
    }

    public Duration getTotalPlayTime(final List<GameHistory> gameHistories, final Account account) {
        Duration totalPlayTime = Duration.ZERO;
        for (final GameHistory gameHistory : gameHistories) {
            totalPlayTime = totalPlayTime.plus(gameHistory.getDuration());
        }
        return totalPlayTime;
    }

    public Map<GameMode, Integer> getGamesPlayed(final List<GameHistory> gameHistories, final Account account) {
        final Map<GameMode, Integer> gamesPlayed = new HashMap<>();
        for (final GameHistory gameHistory : gameHistories) {
            final GameMode gameMode = gameHistory.getGameMode();
            if (gamesPlayed.containsKey(gameMode)) {
                gamesPlayed.put(gameMode, gamesPlayed.get(gameMode) + 1);
            } else {
                gamesPlayed.put(gameMode, 1);
            }
        }
        return gamesPlayed;
    }
}
