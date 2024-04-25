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
    private boolean connected = false;
    private boolean authenticated = false;
    private String roomName = "GLOBAL";
    private String reply = "";

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
                    if (!this.connected && message.startsWith("[HANDSHAKE]")) {
                        final String handshakeClientName = message.substring(12);
                        if (!handshakeClientName.isBlank()) {
                            if (!this.server.containsClient(handshakeClientName)) {
                                this.setName(handshakeClientName);
                                System.out.println("[INFO] Client '" + this.getName() + "' has been handshake!");
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
        if (this.connected) {
            this.disconnectClient();
        }
        System.out.println("[INFO] Client disconnected: " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort());
    }

    private void handleMessage(final String message) {
        if (message != null && !message.isBlank()) {
            final String trimedMessage = message.trim();
            if (!this.authenticated) {
                if (this.server.getAccountManager().authenticate(this.getName(), trimedMessage)) {
                    this.sendMessage("[INFO] You have been successfully authenticated!");
                    this.authenticated = true;
                    this.connectClient();
                } else {
                    this.sendMessage("[ERROR] Authentication failed! Please try again!");
                }
            } else {
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
                    this.server.broadcastMessage(this, this.getName() + ": " + trimedMessage);
                }
            }
        }
    }

    public void sendMessage(final String message) {
        if (message != null && !message.isBlank()) {
            this.printWriter.println(message);
        }
    }

    public void sendPrivateMessage(final ServerClientThread client, final String message) {
        if (client != null && message != null && !message.isBlank()) {
            this.sendMessage("[" + client.getName() + "] -->: " + message);
            client.sendMessage("[" + this.getName() + "] <--: " + message);
            client.setReply(this.getName());
        }
    }

    public void connectClient() {
        this.connected = true;
        this.server.addClient(this);
        this.server.broadcastMessage(this, "-> " + this.getName() + " has connected to the chat-server!");
    }

    public void disconnectClient() {
        this.server.broadcastMessage(this, "<- " + this.getName() + " has disconnected from the chat-server!");
        this.server.getGameManager().removeAllGameRequests(this);
        for (final ServerClientThread client : this.server.getClients()) {
            if (client.getReply().equalsIgnoreCase(this.getName())) {
                client.setReply("");
            }
        }
        if (!this.roomName.equalsIgnoreCase("GLOBAL")) {
            final Room room = this.server.getRoomManager().getRoom(this.roomName);
            if (room != null) {
                this.server.getRoomManager().leaveRoom(room, false, this);

            }
        }
        this.server.removeClient(this);
        try {
            this.socket.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setRoomName(final String roomName) {
        if (roomName != null && !roomName.isBlank()) {
            this.roomName = roomName;
        }
    }

    public String getReply() {
        return this.reply;
    }

    public void setReply(final String reply) {
        if (reply != null && !reply.isBlank()) {
            this.reply = reply;
        }
    }
}
