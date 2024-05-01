package de.dhbw.consoleconnect.server;

public interface ServerClient {

    void connectClient();

    void disconnectClient();

    void sendMessage(final String message);

    void sendPrivateMessage(final ServerClient client, final String message);

    String getName();

    String getRoomName();

    void setRoomName(final String roomName);

    String getReply();

    void setReply(final String reply);
}
