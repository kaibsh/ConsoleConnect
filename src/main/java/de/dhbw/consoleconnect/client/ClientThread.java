package de.dhbw.consoleconnect.client;

import de.dhbw.consoleconnect.client.file.FileHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {

    private final Client client;
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final List<String> messages = new ArrayList<>();
    private String roomName = "GLOBAL";

    public ClientThread(final Client client, final Socket socket) throws IOException {
        this.client = client;
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                final String message = this.bufferedReader.readLine();
                if (message != null && !message.isBlank()) {
                    if (message.equals("[SaveCommand] SAVE")) {
                        this.displayMessage("[SaveCommand] Successfully saved the chat history.");
                        this.saveMessages();
                    } else if (message.startsWith("[RoomManager] ENTER: ")) {
                        this.roomName = message.replace("[RoomManager] ENTER: ", "");
                    } else if (message.equals("[RoomManager] LEAVE: GLOBAL")) {
                        this.roomName = "GLOBAL";
                    } else {
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
            exception.printStackTrace();
        } catch (final IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                this.bufferedReader.close();
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
        this.saveMessages();
    }

    private void displayMessage(final String rawMessage) {
        if (rawMessage != null && !rawMessage.isBlank()) {
            final String message = rawMessage.startsWith("[") ? rawMessage : "(" + this.roomName + ") " + rawMessage;
            this.messages.add(message);
            System.out.println(message);
        }
    }

    private void saveMessages() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String message : this.messages) {
            stringBuilder.append(message).append("\n");
        }
        FileHelper.write("./build/history.txt", stringBuilder.toString());
    }

    public String getRoomName() {
        return this.roomName;
    }

    public List<String> getMessages() {
        return this.messages;
    }
}
