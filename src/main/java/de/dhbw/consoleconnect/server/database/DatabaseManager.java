package de.dhbw.consoleconnect.server.database;

import de.dhbw.consoleconnect.server.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DatabaseManager {

    private final Server server;
    private final List<Database> databases = new LinkedList<>();
    private Connection connection;

    public DatabaseManager(final Server server) {
        this.server = server;
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

    public void registerDatabase(final Database database) {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                if (!this.databases.contains(database)) {
                    database.initialize(this.connection);
                    this.databases.add(database);
                    System.out.println("[DATABASE] Successfully initialized database: " + database.getClass().getSimpleName());
                }
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }
}
