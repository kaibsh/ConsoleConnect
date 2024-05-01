package de.dhbw.consoleconnect.server.database.h2;

import de.dhbw.consoleconnect.server.database.DatabaseService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DatabaseService implements DatabaseService<H2Database> {

    private Connection connection;

    public H2DatabaseService() {
        this.connect();
    }

    private void connect() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = DriverManager.getConnection("jdbc:h2:./build/database");
                System.out.println("[DATABASE] Successfully connected to database!");
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public final void registerDatabase(final H2Database h2Database) {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                h2Database.initialize(this.connection);
                System.out.println("[DATABASE] Successfully initialized database: " + h2Database.getClass().getSimpleName());
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }
}
