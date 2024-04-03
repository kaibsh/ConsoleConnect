package de.dhbw.consoleconnect.server.command;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;

public abstract class Command {

    private final String name;
    private final String description;


    public Command(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void execute(final Server server, final ServerClientThread client, final String[] arguments);

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return description;
    }
}
