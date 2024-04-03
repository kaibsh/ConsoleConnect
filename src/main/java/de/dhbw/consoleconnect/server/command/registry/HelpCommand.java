package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.command.Command;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Shows all available commands.");
    }

    @Override
    public void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments == null) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[HelpCommand] Available commands:").append("\n");
            for (final Command command : server.getCommandHandler().getCommands()) {
                stringBuilder.append("[HelpCommand] - ").append(command.getName()).append(" | ").append(command.getDescription()).append("\n");
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            client.sendMessage(stringBuilder.toString());
        } else {
            client.sendMessage("[HelpCommand] This command does not take any arguments.");
        }
    }
}