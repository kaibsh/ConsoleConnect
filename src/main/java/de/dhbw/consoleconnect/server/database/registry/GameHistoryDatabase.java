package de.dhbw.consoleconnect.server.database.registry;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.database.Database;
import de.dhbw.consoleconnect.server.game.GameHistory;
import de.dhbw.consoleconnect.server.game.GameMode;

import java.sql.*;
import java.util.*;

public class GameHistoryDatabase extends Database {

    @Override
    protected void createTables() {
        try (final Statement statement = this.getConnection().createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS game_history (" +
                    "id UUID NOT NULL DEFAULT RANDOM_UUID() PRIMARY KEY," +
                    "start_time TIMESTAMP NOT NULL," +
                    "end_time TIMESTAMP NOT NULL," +
                    "game_mode VARCHAR(255) NOT NULL," +
                    "draw BOOLEAN NOT NULL DEFAULT FALSE" +
                    ")"
            );
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        try (final Statement statement = this.getConnection().createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS game_history_players (" +
                    "game_id UUID NOT NULL," +
                    "account_id INT NOT NULL," +
                    "winner BOOLEAN NOT NULL DEFAULT FALSE," +
                    "PRIMARY KEY (game_id, account_id)," +
                    "FOREIGN KEY (game_id) REFERENCES game_history(id)," +
                    "FOREIGN KEY (account_id) REFERENCES accounts(id)" +
                    ")"
            );
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public GameHistory selectGameHistory(final UUID gameId) {
        try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM game_history WHERE id = ?")) {
            preparedStatement.setString(1, gameId.toString());
            preparedStatement.execute();
            try (final ResultSet resultSet = preparedStatement.getResultSet()) {
                if (resultSet.next()) {
                    final GameHistory gameHistory = new GameHistory();
                    gameHistory.setId(UUID.fromString(resultSet.getString("id")));
                    gameHistory.setStartTime(resultSet.getTimestamp("start_time").toInstant());
                    gameHistory.setEndTime(resultSet.getTimestamp("end_time").toInstant());
                    gameHistory.setGameMode(GameMode.valueOf(resultSet.getString("game_mode")));
                    gameHistory.setDraw(resultSet.getBoolean("draw"));
                    return gameHistory;
                }
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public List<UUID> selectGameHistoryIDs(final Account account) {
        final List<UUID> gameIds = new ArrayList<>();
        try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM game_history_players WHERE account_id = ?")) {
            preparedStatement.setInt(1, account.getId());
            preparedStatement.execute();
            try (final ResultSet resultSet = preparedStatement.getResultSet()) {
                while (resultSet.next()) {
                    gameIds.add(UUID.fromString(resultSet.getString("game_id")));
                }
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return gameIds;
    }

    public Map<Integer, Boolean> selectGameHistoryPlayers(final UUID gameId) {
        final Map<Integer, Boolean> players = new HashMap<>();
        try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM game_history_players WHERE game_id = ?")) {
            preparedStatement.setString(1, gameId.toString());
            preparedStatement.execute();
            try (final ResultSet resultSet = preparedStatement.getResultSet()) {
                while (resultSet.next()) {
                    players.put(resultSet.getInt("account_id"), resultSet.getBoolean("winner"));
                }
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return players;
    }

    public void insertGameHistory(final GameHistory gameHistory) {
        if (gameHistory != null) {
            try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("INSERT INTO game_history (id, start_time, end_time, game_mode, draw) VALUES (?, ?, ?, ?, ?)")) {
                preparedStatement.setString(1, gameHistory.getId().toString());
                preparedStatement.setTimestamp(2, Timestamp.from(gameHistory.getStartTime()));
                preparedStatement.setTimestamp(3, Timestamp.from(gameHistory.getEndTime()));
                preparedStatement.setString(4, gameHistory.getGameMode().name());
                preparedStatement.setBoolean(5, gameHistory.isDraw());
                preparedStatement.execute();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
            if (gameHistory.getPlayers() != null && !gameHistory.getPlayers().isEmpty()) {
                for (final Map.Entry<Account, Boolean> entry : gameHistory.getPlayers().entrySet()) {
                    try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("INSERT INTO game_history_players (game_id, account_id, winner) VALUES (?, ?, ?)")) {
                        preparedStatement.setString(1, gameHistory.getId().toString());
                        preparedStatement.setInt(2, entry.getKey().getId());
                        preparedStatement.setBoolean(3, entry.getValue());
                        preparedStatement.execute();
                    } catch (final SQLException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
}
