package de.dhbw.consoleconnect.server.database;

public interface DatabaseService<T extends Database> {

    void registerDatabase(final T database);
}
