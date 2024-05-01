package de.dhbw.consoleconnect.server.database.h2;

import de.dhbw.consoleconnect.server.database.Database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class H2Database implements Database<Connection> {

    private Connection connection;

    protected abstract void createTables();

    @Override
    public final void initialize(final Connection connection) {
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

    @Override
    public final Connection getType() {
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
