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
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(32) NOT NULL," +
                    "password VARCHAR(32) NOT NULL" +
                    ")"
            );
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Account selectAccount(final String name) {
        if (name != null && !name.isBlank()) {
            try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM accounts WHERE name = ?")) {
                preparedStatement.setString(1, name);
                preparedStatement.execute();
                try (final ResultSet resultSet = preparedStatement.getResultSet()) {
                    if (resultSet.next()) {
                        final Account account = new Account();
                        account.setId(resultSet.getInt("id"));
                        account.setName(resultSet.getString("name"));
                        account.setPassword(resultSet.getString("password"));
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
            try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("INSERT INTO accounts (name, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, account.getName());
                preparedStatement.setString(2, account.getPassword());
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
            try (final PreparedStatement preparedStatement = this.getConnection().prepareStatement("UPDATE accounts SET name = ? AND password = ? WHERE id = ?")) {
                preparedStatement.setString(1, account.getName());
                preparedStatement.setString(2, account.getPassword());
                preparedStatement.setInt(3, account.getId());
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
