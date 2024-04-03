package de.dhbw.consoleconnect.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ChatClientThread extends Thread {

    private final ChatClient chatClient;
    private final Socket socket;
    private final BufferedReader bufferedReader;

    public ChatClientThread(final ChatClient chatClient, final Socket socket) throws IOException {
        this.chatClient = chatClient;
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                final String message = this.bufferedReader.readLine();
                if (message != null) {
                    System.out.println(message);
                } else {
                    System.out.println("You lost the connection to the chat-server.");
                    this.socket.close();
                    break;
                }
            }
        } catch (final SocketException exception) {
            System.out.println("You lost the connection to the chat-server.");
            exception.printStackTrace();
        } catch (final IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return this.socket;
    }
}
