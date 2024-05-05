package de.dhbw.consoleconnect.server;

public final class ServerClientMock implements ServerClient {

    private final Server server;
    private final String name;
    private String roomName = "GLOBAL";
    private String reply = "";

    public ServerClientMock(final Server server, final String name) {
        this.server = server;
        this.name = "CLIENT-" + name;
        this.connectClient();
    }

    @Override
    public void connectClient() {
        this.server.addClient(this);
    }

    @Override
    public void disconnectClient() {
        this.server.removeClient(this);
    }

    @Override
    public void sendMessage(final String message) {
        // Not implemented!
    }

    @Override
    public void sendPrivateMessage(final ServerClient client, final String message) {
        // Not implemented!
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getRoomName() {
        return this.roomName;
    }

    @Override
    public void setRoomName(final String roomName) {
        if (roomName != null && !roomName.isBlank()) {
            this.roomName = roomName;
        }
    }

    @Override
    public String getReply() {
        return this.reply;
    }

    @Override
    public void setReply(final String reply) {
        if (reply != null && !reply.isBlank()) {
            this.reply = reply;
        }
    }
}
