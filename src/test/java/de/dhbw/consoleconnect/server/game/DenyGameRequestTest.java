package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DenyGameRequestTest {

    @Test
    public void testDenyGameRequest() {
        final ServerMock server = new ServerMock();
        final ServerClient senderClient = new ServerClientMock(server, "DenyGameRequestTest-Sender");
        final ServerClient receiverClient = new ServerClientMock(server, "DenyGameRequestTest-Receiver");

        server.getGameManager().startGameRequest(GameMode.ROCK_PAPER_SCISSOR, senderClient, receiverClient);

        server.getGameManager().denyGameRequest(receiverClient, senderClient, false);

        boolean sendedGameRequestExistent = server.getGameManager().isSendedGameRequestExistent(senderClient, receiverClient);
        assertFalse(sendedGameRequestExistent);

        boolean receivedGameRequestExistent = server.getGameManager().isReceivedGameRequestExistent(receiverClient, senderClient);
        assertFalse(receivedGameRequestExistent);
    }
}
