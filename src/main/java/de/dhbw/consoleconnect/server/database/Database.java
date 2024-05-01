package de.dhbw.consoleconnect.server.database;

public interface Database<T> {

    void initialize(final T type);

    T getType();
}
