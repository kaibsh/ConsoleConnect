package de.dhbw.consoleconnect.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServerThread extends Thread {

    private final ChatServer chatServer;
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private String clientName;

    public ChatServerThread(final ChatServer chatServer, final Socket socket) throws IOException {
        this.chatServer = chatServer;
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        System.out.println("[INFO] Client connected: " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort());
        try {
            while (!socket.isClosed()) {
                final String message = this.bufferedReader.readLine();
                if (message != null) {
                    System.out.println(message);
                    if (this.clientName == null && message.startsWith("[HANDSHAKE] <->")) {
                        final String handshakeClientName = message.substring(16);
                        if (!handshakeClientName.isBlank()) {
                            this.clientName = handshakeClientName;
                            if (!this.chatServer.containsClient(this.clientName)) {
                                this.connectClient(this.clientName);
                            }
                        }
                    } else {
                        this.chatServer.broadcastMessage(message);
                    }
                } else {
                    break;
                }
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
        this.disconnectClient();
        System.out.println("[INFO] Client disconnected: " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort());
    }

    public void sendMessage(final String message) {

    }

    public void connectClient(final String clientName) {
        this.chatServer.addClient(clientName, this);
        this.chatServer.broadcastMessage("-> " + clientName + "has connected to the chat-server!");
    }

    public void disconnectClient() {
        this.chatServer.removeClient(this);
        this.chatServer.broadcastMessage("<- " + clientName + "has disconnected from the chat-server!");
        try {
            this.socket.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public Socket getSocket() {
        return this.socket;
    }
}
