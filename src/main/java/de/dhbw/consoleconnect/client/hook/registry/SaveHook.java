package de.dhbw.consoleconnect.client.hook.registry;

import de.dhbw.consoleconnect.client.Client;
import de.dhbw.consoleconnect.client.ClientThread;
import de.dhbw.consoleconnect.client.file.FileHelper;
import de.dhbw.consoleconnect.client.hook.Hook;

public class SaveHook implements Hook {

    @Override
    public boolean execute(final Client client, final ClientThread clientThread, final String message) {
        if (message.equals("[HOOK] SAVE")) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final String clientMessage : clientThread.getMessages()) {
                stringBuilder.append(clientMessage).append("\n");
            }
            FileHelper.write("./build/history.txt", stringBuilder.toString());
            clientThread.displayMessage("[SaveCommand] Successfully saved the chat history.");
            return true;
        }
        return false;
    }
}
