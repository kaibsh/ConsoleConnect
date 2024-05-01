package de.dhbw.consoleconnect.server.command;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.command.registry.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    private final Server server;
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public CommandManager(final Server server) {
        this.server = server;
        this.registerCommands();
    }

    private void registerCommands() {
        final AccountCommand accountCommand = new AccountCommand();
        this.commands.put(accountCommand.getName(), accountCommand);

        final GameCommand gameCommand = new GameCommand();
        this.commands.put(gameCommand.getName(), gameCommand);

        final HelpCommand helpCommand = new HelpCommand();
        this.commands.put(helpCommand.getName(), helpCommand);

        final ListCommand listCommand = new ListCommand();
        this.commands.put(listCommand.getName(), listCommand);

        final LookupCommand lookupCommand = new LookupCommand();
        this.commands.put(lookupCommand.getName(), lookupCommand);

        final MessageCommand messageCommand = new MessageCommand();
        this.commands.put(messageCommand.getName(), messageCommand);

        final ReplyCommand replyCommand = new ReplyCommand();
        this.commands.put(replyCommand.getName(), replyCommand);

        final RoomCommand roomCommand = new RoomCommand();
        this.commands.put(roomCommand.getName(), roomCommand);

        final SaveCommand saveCommand = new SaveCommand();
        this.commands.put(saveCommand.getName(), saveCommand);

        final StatisticsCommand statisticsCommand = new StatisticsCommand();
        this.commands.put(statisticsCommand.getName(), statisticsCommand);

        final StatusCommand statusCommand = new StatusCommand();
        this.commands.put(statusCommand.getName(), statusCommand);
    }

    public void handleCommand(final ServerClient client, final String commandLine) {
        final String[] splittedCommand = commandLine.split(" ");
        final String commandName = splittedCommand[0];
        final Command command = this.commands.get(commandName);
        if (command != null) {
            String[] commandArguments = null;
            if (splittedCommand.length > 1) {
                commandArguments = new String[splittedCommand.length - 1];
                System.arraycopy(splittedCommand, 1, commandArguments, 0, splittedCommand.length - 1);
            }
            command.execute(this.server, client, commandArguments);
            System.out.println("[COMMAND] Client '" + client.getName() + "' executed command '" + commandName + "' with arguments [" + (commandArguments != null ? String.join(", ", commandArguments) : "") + "]");
        } else {
            client.sendMessage("[CommandManager] Unknown command: '" + commandName + "'");
            client.sendMessage("[CommandManager] Use '/help' to see all available commands.");
        }
    }

    public List<Command> getCommands() {
        return List.copyOf(this.commands.values());
    }
}
