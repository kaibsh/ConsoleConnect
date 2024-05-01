package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.command.Command;
import de.dhbw.consoleconnect.server.room.Room;

public class ListCommand extends Command {

    public ListCommand() {
        super("list", "Lists all connected clients.");
    }

    @Override
    protected void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments == null) {
            if (server.getClients().size() - 1 > 0) {
                client.sendMessage("[ListCommand] Connected clients:");
                for (final ServerClientThread targetClient : server.getClients()) {
                    if (targetClient.getRoomName().equalsIgnoreCase("GLOBAL")) {
                        client.sendMessage("[ListCommand] - " + targetClient.getName());
                        final String status = this.getStatus(server, targetClient);
                        if (status != null) {
                            client.sendMessage("[ListCommand]   > " + status);
                        }
                    } else {
                        final Room room = server.getRoomManager().getRoom(targetClient.getRoomName());
                        if (room != null) {
                            if (!room.isGame()) {
                                client.sendMessage("[ListCommand] - " + targetClient.getName() + " @" + room.getName().replace("ROOM-", "") + " (" + room.getClients().size() + ")");
                                final String status = this.getStatus(server, targetClient);
                                if (status != null) {
                                    client.sendMessage("[ListCommand]   > " + status);
                                }
                            } else {
                                client.sendMessage("[ListCommand] - " + targetClient.getName() + " | IN-GAME");
                                final String status = this.getStatus(server, targetClient);
                                if (status != null) {
                                    client.sendMessage("[ListCommand]   > " + status);
                                }
                            }
                        } else {
                            client.sendMessage("[ListCommand] - " + targetClient.getName());
                        }
                    }
                }
            } else {
                client.sendMessage("[ListCommand] There are no other clients connected.");
            }
        } else {
            client.sendMessage("[ListCommand] This command does not take any arguments.");
        }
    }

    private String getStatus(final Server server, final ServerClientThread client) {
        final Account account = server.getAccountManager().getAccountByName(client.getName());
        if (account != null) {
            if (account.getStatus() != null) {
                return account.getStatus();
            }
        }
        return null;
    }
}
