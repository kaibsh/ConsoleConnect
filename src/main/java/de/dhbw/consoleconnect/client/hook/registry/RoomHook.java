package de.dhbw.consoleconnect.client.hook.registry;

import de.dhbw.consoleconnect.client.Client;
import de.dhbw.consoleconnect.client.ClientThread;
import de.dhbw.consoleconnect.client.hook.Hook;

public final class RoomHook implements Hook {

    @Override
    public boolean execute(final Client client, final ClientThread clientThread, final String message) {
        if (message.startsWith("[HOOK] ROOM ->: ")) {
            client.setRoomName(message.replace("[HOOK] ROOM ->: ", ""));
            return true;
        } else if (message.equals("[HOOK] ROOM <-: GLOBAL")) {
            client.setRoomName("GLOBAL");
            return true;
        }
        return false;
    }
}
