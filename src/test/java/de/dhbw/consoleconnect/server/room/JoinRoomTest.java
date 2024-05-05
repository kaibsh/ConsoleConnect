package de.dhbw.consoleconnect.server.room;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoinRoomTest {

    @Test
    public void testJoinRoom() {
        final ServerMock server = new ServerMock();
        final ServerClient addClient = new ServerClientMock(server, "JoinRoomTest-Add");
        final ServerClient joinClient = new ServerClientMock(server, "JoinRoomTest-Join");

        final Room room = new Room("JoinRoomTest", false);
        server.getRoomManager().addRoom(room, addClient);

        server.getRoomManager().joinRoom(room, false, joinClient);

        boolean joined = false;
        for (final ServerClient roomClient : room.getClients()) {
            if (roomClient.getName().equals(joinClient.getName())) {
                joined = true;
            }
        }
        assertTrue(joined);
    }
}
