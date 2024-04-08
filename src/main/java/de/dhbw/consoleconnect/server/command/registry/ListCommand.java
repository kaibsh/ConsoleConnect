package de.dhbw.consoleconnect.server.command.registry;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClientThread;
import de.dhbw.consoleconnect.server.command.Command;
import de.dhbw.consoleconnect.server.room.Room;

public class ListCommand extends Command {

    public ListCommand() {
        super("list", "Lists all connected clients.");
    }

    @Override
    protected void execute(final Server server, final ServerClientThread client, final String[] arguments) {
        if (arguments == null) {
            for (final ServerClientThread targetClient : server.getClients()) {
                if (targetClient.getRoomName().equalsIgnoreCase("GLOBAL")) {
                    client.sendMessage("[ListCommand] - " + targetClient.getClientName());
                } else {
                    final Room room = server.getRoomManager().getRoom(targetClient.getRoomName());
                    if (room != null) {
                        if (!room.isGame()) {
                            client.sendMessage("[ListCommand] - " + targetClient.getClientName() + " @" + room.getName() + " (" + room.getClients().size() + ")");
                        } else {
                            client.sendMessage("[ListCommand] - " + targetClient.getClientName() + " | IN-GAME");
                        }
                    } else {
                        client.sendMessage("[ListCommand] - " + targetClient.getClientName());
                    }
                }
            }
        } else {
            client.sendMessage("[ListCommand] This command does not take any arguments.");
        }
    }
}
