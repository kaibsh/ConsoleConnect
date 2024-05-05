package de.dhbw.consoleconnect.client.hook;

import de.dhbw.consoleconnect.client.Client;
import de.dhbw.consoleconnect.client.ClientThread;
import de.dhbw.consoleconnect.client.hook.registry.ClearHook;
import de.dhbw.consoleconnect.client.hook.registry.RoomHook;
import de.dhbw.consoleconnect.client.hook.registry.SaveHook;

import java.util.LinkedList;
import java.util.List;

public final class HookManager {

    private final Client client;
    private final List<Hook> hooks = new LinkedList<>();

    public HookManager(final Client client) {
        this.client = client;
        this.registerHooks();
    }

    private void registerHooks() {
        this.hooks.add(new ClearHook());
        this.hooks.add(new RoomHook());
        this.hooks.add(new SaveHook());
    }

    public boolean handleHook(final ClientThread clientThread, final String message) {
        for (final Hook hook : this.hooks) {
            if (hook.execute(this.client, clientThread, message)) {
                return true;
            }
        }
        return false;
    }
}
