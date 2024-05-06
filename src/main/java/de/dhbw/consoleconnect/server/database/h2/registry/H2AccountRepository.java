package de.dhbw.consoleconnect.server.database.h2.registry;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.database.repositories.AccountRepository;

import java.sql.*;

public final class H2AccountRepository implements AccountRepository<Connection> {

    private Connection connection;

    @Override
    public void initialize(final Connection connection) {
        this.connection = connection;
        try (final Statement statement = this.connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(255) NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "status VARCHAR(255) NULL DEFAULT 'Hey, I am using ConsoleConnect!'" +
                    ")"
            );
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Account getAccountById(final int accountId) {
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
            preparedStatement.setInt(1, accountId);
            return this.getAccount(preparedStatement);
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public Account getAccountByName(final String accountName) {
        if (accountName != null && !accountName.isBlank()) {
            try (final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM accounts WHERE name = ?")) {
                preparedStatement.setString(1, accountName.toLowerCase());
                return this.getAccount(preparedStatement);
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private Account getAccount(final PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.execute();
        try (final ResultSet resultSet = preparedStatement.getResultSet()) {
            if (resultSet.next()) {
                final Account account = new Account();
                account.setId(resultSet.getInt("id"));
                account.setName(resultSet.getString("name"));
                account.setPassword(resultSet.getString("password"));
                account.setStatus(resultSet.getString("status"));
                return account;
            }
        }
        return null;
    }

    @Override
    public void saveAccount(final Account account) {
        if (account != null) {
            final boolean exists = this.getAccountByName(account.getName()) != null;
            if (exists) {
                try (final PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE accounts SET name = ?, password = ?, status = ? WHERE id = ?")) {
                    preparedStatement.setString(1, account.getName());
                    preparedStatement.setString(2, account.getPassword());
                    preparedStatement.setString(3, account.getStatus());
                    preparedStatement.setInt(4, account.getId());
                    preparedStatement.execute();
                } catch (final SQLException exception) {
                    exception.printStackTrace();
                }
            } else {
                try (final PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO accounts (name, password, status) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, account.getName());
                    preparedStatement.setString(2, account.getPassword());
                    preparedStatement.setString(3, account.getStatus() != null ? account.getStatus() : "Hey, I am using ConsoleConnect!");
                    preparedStatement.execute();
                    try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                        if (resultSet.next()) {
                            account.setId(resultSet.getInt(1));
                        }
                    }
                } catch (final SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public void deleteAccount(final Account account) {
        if (account != null) {
            try (final PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM accounts WHERE id = ?")) {
                preparedStatement.setInt(1, account.getId());
                preparedStatement.execute();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}
