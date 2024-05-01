package de.dhbw.consoleconnect.server.command;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;

public abstract class Command {

    private final String name;
    private final String description;

    protected Command(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    protected abstract void execute(final Server server, final ServerClient client, final String[] arguments);

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }
}
