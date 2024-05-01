package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.command.Command;

public class MessageCommand extends Command {

    public MessageCommand() {
        super("message", "Sends a message to a specific client.");
    }

    @Override
    protected void execute(final Server server, final ServerClient client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[MessageCommand] Usage: /message <clientName> <message>");
        } else if (arguments.length >= 2) {
            final String receiverClientName = arguments[0];
            if (!receiverClientName.isBlank()) {
                if (!client.getName().equalsIgnoreCase(receiverClientName)) {
                    final ServerClient receiverClient = server.getClient(receiverClientName);
                    if (receiverClient != null) {
                        if (!server.getGameManager().isInGame(receiverClient)) {
                            final String message = String.join(" ", arguments).replace(receiverClientName + " ", "");
                            if (!message.isBlank()) {
                                client.sendPrivateMessage(receiverClient, message);
                            } else {
                                client.sendMessage("[MessageCommand] The message cannot be empty!");
                            }
                        } else {
                            client.sendMessage("[MessageCommand] The specified client is currently in a game!");
                        }
                    } else {
                        client.sendMessage("[MessageCommand] The specified client does not exist!");
                    }
                } else {
                    client.sendMessage("[MessageCommand] You cannot send a message to yourself!");
                }
            } else {
                client.sendMessage("[MessageCommand] Usage: '/message <clientName> <message>'");
            }
        } else {
            client.sendMessage("[MessageCommand] Usage: /message <clientName> <message>");
        }
    }
}
