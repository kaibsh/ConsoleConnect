package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.command.Command;

public class StatusCommand extends Command {

    public StatusCommand() {
        super("status", "Change your status.");
    }

    @Override
    protected void execute(final Server server, final ServerClient client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[StatusCommand] Usage: /status <statusMessage>");
        } else if (arguments.length >= 1) {
            final String statusMessage = String.join(" ", arguments);
            if (!statusMessage.isBlank()) {
                if (server.getAccountManager().changeStatus(client.getName(), statusMessage)) {
                    client.sendMessage("[StatusCommand] Your successfully changed your status!");
                } else {
                    client.sendMessage("[StatusCommand] Unable to change your status!");
                }
            } else {
                client.sendMessage("[StatusCommand] The message cannot be empty!");
            }
        } else {
            client.sendMessage("[StatusCommand] Usage: /status <statusMessage>");
        }
    }
}
