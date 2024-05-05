package de.dhbw.consoleconnect.server.room;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RemoveRoomTest {

    @Test
    public void testRemoveRoom() {
        final ServerMock server = new ServerMock();
        final ServerClient client = new ServerClientMock(server, "RemoveRoomTest");

        final Room room = new Room("RemoveRoomTest", false);
        server.getRoomManager().addRoom(room, client);

        server.getRoomManager().removeRoom(room);

        assertFalse(server.getRoomManager().isRoomExistent(room.getName()));

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
