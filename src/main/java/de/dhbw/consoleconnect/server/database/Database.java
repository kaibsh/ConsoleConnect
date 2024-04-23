package de.dhbw.consoleconnect.server.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

    private Connection connection;

    protected abstract void createTables();

    protected final void initialize(final Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                this.connection = connection;
                this.createTables();
            } else {
                throw new IllegalStateException("Connection is not available!");
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    protected Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                return this.connection;
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        throw new IllegalStateException("Connection is not available!");
    }
}
