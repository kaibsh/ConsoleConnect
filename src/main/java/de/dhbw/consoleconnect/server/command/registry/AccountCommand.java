package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.command.Command;

public class AccountCommand extends Command {

    public AccountCommand() {
        super("account", "Manage your account.");
    }

    @Override
    protected void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments == null) {
            client.sendMessage("[AccountCommand] Use '/account help' for more information.");
        } else if (arguments.length == 1) {
            if (arguments[0].equalsIgnoreCase("help")) {
                client.sendMessage("[AccountCommand] Help:");
                client.sendMessage("[AccountCommand] - '/account delete' | Delete your account.");
                client.sendMessage("[AccountCommand] - '/account details' | Show your account details.");
                client.sendMessage("[AccountCommand] - '/account change-password <newPassword>' | Change your password.");
            } else if (arguments[0].equalsIgnoreCase("delete")) {
                final Account account = server.getAccountManager().getAccountByName(client.getName());
                if (account != null) {
                    if (server.getAccountManager().deleteAccount(account)) {
                        client.sendMessage("[AccountCommand] Successfully deleted account!");
                        client.disconnectClient();
                    } else {
                        client.sendMessage("[AccountCommand] Failed to delete account!");
                    }
                } else {
                    client.sendMessage("[AccountCommand] Account not found!");
                }
            } else if (arguments[0].equalsIgnoreCase("details")) {
                final Account account = server.getAccountManager().getAccountByName(client.getName());
                if (account != null) {
                    client.sendMessage("[AccountCommand] Details:");
                    client.sendMessage("[AccountCommand] - Name: " + account.getName());
                    client.sendMessage("[AccountCommand] - Password: " + account.getPassword());
                    client.sendMessage("[AccountCommand] - Status: " + account.getStatus());
                } else {
                    client.sendMessage("[AccountCommand] Account not found!");
                }
            } else {
                client.sendMessage("[AccountCommand] Use '/account help' for more information.");
            }
        } else if (arguments.length == 2) {
            if (arguments[0].equalsIgnoreCase("change-password")) {
                final String newPassword = arguments[1];
                if (!newPassword.isBlank()) {
                    if (server.getAccountManager().changePassword(client.getName(), newPassword)) {
                        client.sendMessage("[AccountCommand] Successfully changed password!");
                    } else {
                        client.sendMessage("[AccountCommand] Failed to change password!");
                    }
                } else {
                    client.sendMessage("[AccountCommand] Usage: '/account change-password <newPassword>'");
                }
            } else {
                client.sendMessage("[AccountCommand] Use '/account help' for more information.");
            }
        } else {
            client.sendMessage("[AccountCommand] Use '/account help' for more information.");
        }
    }
}
