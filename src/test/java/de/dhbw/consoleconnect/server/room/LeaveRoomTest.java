package de.dhbw.consoleconnect.server.room;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LeaveRoomTest {

    @Test
    public void testLeaveRoom() {
        final ServerMock server = new ServerMock();
        final ServerClient client = new ServerClientMock(server, "LeaveRoomTest");

        final Room room = new Room("LeaveRoomTest", false);
        server.getRoomManager().addRoom(room, client);

        server.getRoomManager().leaveRoom(room, false, client);

        boolean leaved = true;
        for (final ServerClient roomClient : room.getClients()) {
            if (roomClient.getName().equals(client.getName())) {
                leaved = false;
                break;
            }
        }
        assertTrue(leaved);
    }
}
