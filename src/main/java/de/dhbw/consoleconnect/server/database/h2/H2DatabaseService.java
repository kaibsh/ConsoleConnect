package de.dhbw.consoleconnect.server.database.h2;

import de.dhbw.consoleconnect.server.database.Database;
import de.dhbw.consoleconnect.server.database.DatabaseService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class H2DatabaseService implements DatabaseService<Connection> {

    private Connection connection;

    public H2DatabaseService() {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:./build/database");
            System.out.println("[DATABASE] Successfully connected to database!");
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void registerDatabase(final Database<Connection> database) {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                database.initialize(this.connection);
                System.out.println("[DATABASE] Successfully initialized database: " + database.getClass().getSimpleName());
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }
}
