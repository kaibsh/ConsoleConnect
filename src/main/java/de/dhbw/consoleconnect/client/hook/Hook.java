package de.dhbw.consoleconnect.client.hook;

import de.dhbw.consoleconnect.client.Client;
import de.dhbw.consoleconnect.client.ClientThread;

public interface Hook {

    boolean execute(final Client client, final ClientThread clientThread, final String message);
}
