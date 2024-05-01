package de.dhbw.consoleconnect.server.database.registry;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDatabase extends Database {

    @Override
    protected void createTables() {
        try (final Statement statement = this.getConnection().createStatement()) {
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

    public Account selectAccountById(final int accountId) {
        try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
            preparedStatement.setInt(1, accountId);
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
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Account selectAccountByName(final String accountName) {
        if (accountName != null && !accountName.isBlank()) {
            try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM accounts WHERE name = ?")) {
                preparedStatement.setString(1, accountName.toLowerCase());
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
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public void insertAccount(final Account account) {
        if (account != null) {
            try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("INSERT INTO accounts (name, password, status) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
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

    public void updateAccount(final Account account) {
        if (account != null) {
            try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("UPDATE accounts SET name = ?, password = ?, status = ? WHERE id = ?")) {
                preparedStatement.setString(1, account.getName());
                preparedStatement.setString(2, account.getPassword());
                preparedStatement.setString(3, account.getStatus());
                preparedStatement.setInt(4, account.getId());
                preparedStatement.execute();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void deleteAccount(final Account account) {
        if (account != null) {
            try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("DELETE FROM accounts WHERE id = ?")) {
                preparedStatement.setInt(1, account.getId());
                preparedStatement.execute();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}
