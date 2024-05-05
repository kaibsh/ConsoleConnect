package de.dhbw.consoleconnect.server.room;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddRoomTest {

    @Test
    public void testAddRoom() {
        final ServerMock server = new ServerMock();
        final ServerClient client = new ServerClientMock(server, "AddRoomTest");

        final Room room = new Room("AddRoomTest", false);
        server.getRoomManager().addRoom(room, client);

        assertTrue(server.getRoomManager().isRoomExistent(room.getName()));
        for (final ServerClient roomClient : room.getClients()) {
            assertEquals(client.getName(), roomClient.getName());
            assertEquals(room.getName(), roomClient.getRoomName());
        }
    }
}
