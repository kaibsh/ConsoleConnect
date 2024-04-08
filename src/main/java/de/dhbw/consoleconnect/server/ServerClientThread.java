package de.dhbw.consoleconnect.server;

import de.dhbw.consoleconnect.server.game.Game;
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
            while (!this.socket.isClosed()) {
                final String message = this.bufferedReader.readLine();
                if (message != null) {
                    if (this.clientName == null && message.startsWith("[HANDSHAKE]")) {
                        final String handshakeClientName = message.substring(12);
                        if (!handshakeClientName.isBlank()) {
                            if (!this.server.containsClient(handshakeClientName)) {
                                this.clientName = handshakeClientName;
                                this.connectClient();
                                System.out.println(message);
                            } else {
                                this.sendMessage("[ERROR] The client name is already in use!");
                                this.socket.close();
                            }
                        }
                    } else {
                        this.handleMessage(message);
                    }
                } else {
                    break;
                }
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
        if (this.clientName != null) {
            this.disconnectClient();
            System.out.println("[INFO] Client disconnected: " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort());
        }
    }

    private void handleMessage(final String message) {
        if (message != null && !message.isBlank()) {
            final String trimedMessage = message.trim();
            if (!this.roomName.equalsIgnoreCase("GLOBAL")) {
                final Room room = this.server.getRoomManager().getRoom(this.roomName);
                if (room != null) {
                    if (room.isGame()) {
                        final Game game = this.server.getGameManager().getGame(this);
                        if (game != null) {
                            this.server.getGameManager().handleGameInput(game, this, trimedMessage);
                            return;
                        }
                    }
                }
            }
            if (trimedMessage.startsWith("/") && trimedMessage.length() > 1) {
                this.server.getCommandHandler().handleCommand(this, trimedMessage.substring(1));
            } else {
                this.server.broadcastMessage(this, this.clientName + ": " + trimedMessage);
            }
        }
    }

    public void sendMessage(final String message) {
        if (message != null && !message.isBlank()) {
            this.printWriter.println(message);
        }
    }

    public void connectClient() {
        this.server.addClient(this.clientName, this);
        this.server.broadcastMessage(this, "-> " + this.clientName + " has connected to the chat-server!");
    }

    public void disconnectClient() {
        this.server.removeClient(this);
        this.server.getGameManager().removeAllGameRequests(this);
        if (!this.roomName.equalsIgnoreCase("GLOBAL")) {
            final Room room = this.server.getRoomManager().getRoom(this.roomName);
            if (room != null) {
                this.server.getRoomManager().leaveRoom(room, this);
            }
        }
        this.server.broadcastMessage(this, "<- " + this.clientName + " has disconnected from the chat-server!");
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
        return this.roomName;
    }

    public void setRoomName(final String roomName) {
        if (roomName != null && !roomName.isBlank()) {
            this.roomName = roomName;
        }
    }
}
