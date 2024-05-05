package de.dhbw.consoleconnect.client.hook.registry;

import de.dhbw.consoleconnect.client.Client;
import de.dhbw.consoleconnect.client.ClientThread;
import de.dhbw.consoleconnect.client.hook.Hook;

public final class ClearHook implements Hook {

    @Override
    public boolean execute(final Client client, final ClientThread clientThread, final String message) {
        if (message.equals("[HOOK] CLEAR")) {
            clientThread.clearMessages();
            return true;
        }
        return false;
    }
}
