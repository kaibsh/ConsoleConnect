package de.dhbw.consoleconnect.server.database;

public interface DatabaseService<T> {

    void registerDatabase(final Database<T> database);
}
