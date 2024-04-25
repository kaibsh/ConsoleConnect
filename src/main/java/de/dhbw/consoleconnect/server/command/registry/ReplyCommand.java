package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.command.Command;

public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("reply", "Replies to the last received message.");
    }

    @Override
    protected void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments.length >= 1) {
            if (!client.getReply().isBlank()) {
                final String message = String.join(" ", arguments);
                if (!message.isBlank()) {
                    final ServerClientThread receiverClient = server.getClient(client.getReply());
                    if (receiverClient != null) {
                        client.sendPrivateMessage(receiverClient, message);
                    } else {
                        client.sendMessage("[ReplyCommand] The specified client does not exist!");
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
