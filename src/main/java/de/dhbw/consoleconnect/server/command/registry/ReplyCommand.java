package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.command.Command;

public final class ReplyCommand extends Command {

    public ReplyCommand() {
        super("reply", "Replies to the last received message.");
    }

    @Override
    protected void execute(final Server server, final ServerClient client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[ReplyCommand] Usage: /reply <message>");
        } else if (arguments.length >= 1) {
            if (!client.getReply().isBlank()) {
                final String message = String.join(" ", arguments);
                if (!message.isBlank()) {
                    final ServerClient receiverClient = server.getClient(client.getReply());
                    if (receiverClient != null) {
                        client.sendPrivateMessage(receiverClient, message);
                    } else {
                        client.setReply("");
                        client.sendMessage("[ReplyCommand] The recipient of the message is not longer connected!");
                    }
                } else {
                    client.sendMessage("[ReplyCommand] The message cannot be empty!");
                }
            } else {
                client.sendMessage("[ReplyCommand] You have not received a message yet!");
            }
        } else {
            client.sendMessage("[ReplyCommand] Usage: /reply <message>");
        }
    }
}
