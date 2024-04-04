package de.dhbw.consoleconnect.server;

import de.dhbw.consoleconnect.server.room.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientThread extends Thread {

    private final Server server;
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private String clientName;
    private String roomName = "GLOBAL";

    public ServerClientThread(final Server server, final Socket socket) throws IOException {
        this.server = server;
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
                    if (this.clientName == null && message.startsWith("[HANDSHAKE]")) {
                        final String handshakeClientName = message.substring(12);
                        if (!handshakeClientName.isBlank()) {
                            this.clientName = handshakeClientName;
                            if (!this.server.containsClient(this.clientName)) {
                                this.connectClient(this.clientName);
                                System.out.println(message);
                            }
                        }
                    } else {
                        this.server.handleMessage(this, message);
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
        if (message != null && !message.isBlank()) {
            this.printWriter.println(message);
        }
    }

    public void connectClient(final String clientName) {
        if (clientName != null && !clientName.isBlank()) {
            this.server.addClient(clientName, this);
            this.server.broadcastMessage("-> " + clientName + " has connected to the chat-server!");
        }
    }

    public void disconnectClient() {
        this.server.removeClient(this);
        this.server.broadcastMessage("<- " + clientName + " has disconnected from the chat-server!");
        if (!this.roomName.equalsIgnoreCase("GLOBAL")) {
            final Room room = this.server.getRoomManager().getRoom(this.roomName);
            if (room != null) {
                this.server.getRoomManager().leaveRoom(room, this);
            }
        }
        try {
            this.socket.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public String getClientName() {
        return this.clientName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(final String roomName) {
        if (roomName != null && !roomName.isBlank()) {
            this.roomName = roomName;
        }
    }
}
