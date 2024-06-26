package de.dhbw.consoleconnect.server.database.h2.registry;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.database.repositories.GameHistoryRepository;
import de.dhbw.consoleconnect.server.game.GameMode;
import de.dhbw.consoleconnect.server.game.history.GameHistory;
import de.dhbw.consoleconnect.server.game.history.GameHistoryBuilder;

import java.sql.*;
import java.util.*;

public final class H2GameHistoryRepository implements GameHistoryRepository<Connection> {

    private Connection connection;

    @Override
    public void initialize(final Connection connection) {
        this.connection = connection;
        try (final Statement statement = this.connection.createStatement()) {
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
        try (final Statement statement = this.connection.createStatement()) {
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

    @Override
    public GameHistory getGameHistory(final UUID gameId) {
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM game_history WHERE id = ?")) {
            preparedStatement.setString(1, gameId.toString());
            preparedStatement.execute();
            try (final ResultSet resultSet = preparedStatement.getResultSet()) {
                if (resultSet.next()) {
                    final GameHistory gameHistory = new GameHistoryBuilder()
                            .setId(UUID.fromString(resultSet.getString("id")))
                            .setStartTime(resultSet.getTimestamp("start_time").toInstant())
                            .setEndTime(resultSet.getTimestamp("end_time").toInstant())
                            .setGameMode(GameMode.valueOf(resultSet.getString("game_mode")))
                            .setDraw(resultSet.getBoolean("draw"))
                            .build();
                    return gameHistory;
                }
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public List<UUID> getGameHistoryIDs(final Account account) {
        final List<UUID> gameIds = new ArrayList<>();
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM game_history_players WHERE account_id = ?")) {
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

    @Override
    public Map<Integer, Boolean> getGameHistoryPlayers(final UUID gameId) {
        final Map<Integer, Boolean> players = new HashMap<>();
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM game_history_players WHERE game_id = ?")) {
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

    @Override
    public void saveGameHistory(final GameHistory gameHistory) {
        if (gameHistory != null) {
            final boolean exists = this.getGameHistory(gameHistory.getId()) != null;
            if (!exists) {
                try (final PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO game_history (id, start_time, end_time, game_mode, draw) VALUES (?, ?, ?, ?, ?)")) {
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
                        try (final PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO game_history_players (game_id, account_id, winner) VALUES (?, ?, ?)")) {
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
}
