package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.command.Command;

public class LookupCommand extends Command {

    public LookupCommand() {
        super("lookup", "Lookup a user.");
    }

    @Override
    protected void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[LookupCommand] Usage: /lookup <clientName>");
        } else if (arguments.length == 1) {
            final String targetClientName = arguments[0];
            if (!targetClientName.isBlank()) {
                final Account account = server.getAccountManager().getAccountByName(targetClientName);
                if (account != null) {
                    client.sendMessage("[LookupCommand] Lookup:");
                    client.sendMessage("[LookupCommand] - Name: " + account.getName());
                    client.sendMessage("[LookupCommand] - Status: " + account.getStatus());
                } else {
                    client.sendMessage("[LookupCommand] Account not found!");
                }
            } else {
                client.sendMessage("[LookupCommand] Usage: /lookup <clientName>");
            }
        } else {
            client.sendMessage("[LookupCommand] Usage: /lookup <clientName>");
        }
    }
}
