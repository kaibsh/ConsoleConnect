package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.command.Command;

public class SaveCommand extends Command {

    public SaveCommand() {
        super("save", "Save the current chat history.");
    }

    @Override
    protected void execute(final Server server, final ServerClient client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[HOOK] SAVE");
        } else {
            client.sendMessage("[SaveCommand] This command does not take any arguments.");
        }
    }
}
