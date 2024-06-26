package de.dhbw.consoleconnect.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public final class ClientThread extends Thread {

    private final Client client;
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final List<String> messages = new ArrayList<>();

    public ClientThread(final Client client, final Socket socket) throws IOException {
        this.client = client;
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (!this.socket.isClosed()) {
                final String message = this.bufferedReader.readLine();
                if (message != null && !message.isBlank()) {
                    if (!this.client.getHookManager().handleHook(this, message)) {
                        this.displayMessage(message);
                    }
                } else {
                    System.out.println("You lost the connection to the chat-server.");
                    System.out.println("Press ENTER for closing the chat-client...");
                    this.socket.close();
                    break;
                }
            }
        } catch (final SocketException exception) {
            System.out.println("You lost the connection to the chat-server.");
            System.out.println("Press ENTER for closing the chat-client...");
        } catch (final IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                this.bufferedReader.close();
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void clearMessages() {
        this.displayMessage("[INFO] Clearing the console...");
        this.messages.clear();
/*      try {
            final String osName = System.getProperty("os.name");
            if (osName.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final IOException exception) {
            System.out.println("Error while clearing the console.");
        }*/
    }

    public void displayMessage(final String rawMessage) {
        if (rawMessage != null && !rawMessage.isBlank()) {
            final String message = rawMessage.startsWith("[") ? rawMessage : "(" + this.client.getRoomName() + ") " + rawMessage;
            this.messages.add(message);
            System.out.println(message);
        }
    }

    public List<String> getMessages() {
        return this.messages;
    }
}
