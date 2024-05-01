package de.dhbw.consoleconnect.server.database.h2.registry;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.account.AdminAccount;
import de.dhbw.consoleconnect.server.database.h2.H2Database;
import de.dhbw.consoleconnect.server.database.repositories.AccountRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class H2AccountRepository extends H2Database implements AccountRepository {

    @Override
    protected final void createTables() {
        try (final Statement statement = this.getType().createStatement()) {
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
    public final Account getAccountById(final int accountId) {
        try (final PreparedStatement preparedStatement = this.getType().prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
            preparedStatement.setInt(1, accountId);
            return this.getAccount(preparedStatement);
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public final Account getAccountByName(final String accountName) {
        if (accountName != null && !accountName.isBlank()) {
            try (final PreparedStatement preparedStatement = this.getType().prepareStatement("SELECT * FROM accounts WHERE name = ?")) {
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
                Account account = null;
                if (resultSet.getString("name").equalsIgnoreCase("*** Admin ***")) {
                    account = new AdminAccount();
                } else {
                    account = new Account();
                }
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
    public final void saveAccount(final Account account) {
        if (account != null) {
            final boolean exists = this.getAccountByName(account.getName()) != null;
            if (exists) {
                try (final PreparedStatement preparedStatement = this.getType().prepareStatement("UPDATE accounts SET name = ?, password = ?, status = ? WHERE id = ?")) {
                    preparedStatement.setString(1, account.getName());
                    preparedStatement.setString(2, account.getPassword());
                    preparedStatement.setString(3, account.getStatus());
                    preparedStatement.setInt(4, account.getId());
                    preparedStatement.execute();
                } catch (final SQLException exception) {
                    exception.printStackTrace();
                }
            } else {
                try (final PreparedStatement preparedStatement = this.getType().prepareStatement("INSERT INTO accounts (name, password, status) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
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
    public final void deleteAccount(final Account account) {
        if (account != null) {
            try (final PreparedStatement preparedStatement = this.getType().prepareStatement("DELETE FROM accounts WHERE id = ?")) {
                preparedStatement.setInt(1, account.getId());
                preparedStatement.execute();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}
