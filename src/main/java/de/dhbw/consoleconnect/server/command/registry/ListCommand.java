package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.command.Command;

public class ListCommand extends Command {

    public ListCommand() {
        super("list", "");
    }

    @Override
    public void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments == null) {

        } else {
            client.sendMessage("[HelpCommand] This command does not take any arguments.");
        }
    }
}
