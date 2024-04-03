package de.dhbw.consoleconnect.server.command;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.command.registry.HelpCommand;
import de.dhbw.consoleconnect.server.command.registry.SaveCommand;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler {

    private final Server server;
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public CommandHandler(final Server server) {
        this.server = server;
        this.registerCommands();
    }

    private void registerCommands() {
        final HelpCommand helpCommand = new HelpCommand();
        this.commands.put(helpCommand.getName(), helpCommand);

        final SaveCommand saveCommand = new SaveCommand();
        this.commands.put(saveCommand.getName(), saveCommand);
    }

    public void handleCommand(final ServerClientThread client, final String commandLine) {
        final String[] splittedCommand = commandLine.split(" ");

        final String commandName = splittedCommand[0];
        final Command command = commands.get(commandName);

        if (command != null) {
            String[] commandArguments = null;
            if (splittedCommand.length > 1) {
                commandArguments = new String[splittedCommand.length - 1];
                System.arraycopy(splittedCommand, 1, commandArguments, 0, splittedCommand.length - 1);
            }

            command.execute(this.server, client, commandArguments);

            System.out.println("[COMMAND] / Client '" + client.getClientName() + "' executed command: '" + commandName + "' with arguments [" + (commandArguments != null ? String.join(", ", commandArguments) : "") + "]");
        } else {
            client.sendMessage("[CommandHandler] Unknown command: '" + commandName + "'");
        }
    }

    public List<Command> getCommands() {
        return List.copyOf(this.commands.values());
    }
}
