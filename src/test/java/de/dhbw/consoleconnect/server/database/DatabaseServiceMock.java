package de.dhbw.consoleconnect.server.database;

import java.util.ArrayList;
import java.util.List;

public final class DatabaseServiceMock implements DatabaseService<List<?>> {

    @Override
    public void registerDatabase(final Database<List<?>> database) {
        database.initialize(new ArrayList<>());
    }
}
