package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.command.Command;

public class SaveCommand extends Command {

    public SaveCommand() {
        super("save", "Save the current chat history.");
    }

    @Override
    public void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[SaveCommand]");
        } else {
            client.sendMessage("[SaveCommand] This command does not take any arguments.");
        }
    }
}