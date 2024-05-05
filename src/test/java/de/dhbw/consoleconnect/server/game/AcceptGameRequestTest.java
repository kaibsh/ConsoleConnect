package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptGameRequestTest {

    @Test
    public void testAcceptGameRequest() {
        final ServerMock server = new ServerMock();
        final ServerClient senderClient = new ServerClientMock(server, "AcceptGameRequestTest-Sender");
        final ServerClient receiverClient = new ServerClientMock(server, "AcceptGameRequestTest-Receiver");

        server.getGameManager().startGameRequest(GameMode.ROCK_PAPER_SCISSOR, senderClient, receiverClient);

        server.getGameManager().acceptGameRequest(receiverClient, senderClient);

        boolean sendedGameRequestExistent = server.getGameManager().isSendedGameRequestExistent(senderClient, receiverClient);
        assertFalse(sendedGameRequestExistent);

        boolean receivedGameRequestExistent = server.getGameManager().isReceivedGameRequestExistent(receiverClient, senderClient);
        assertFalse(receivedGameRequestExistent);

        boolean senderGameExistent = server.getGameManager().isInGame(senderClient);
        assertTrue(senderGameExistent);

        boolean receiverGameExistent = server.getGameManager().isInGame(receiverClient);
        assertTrue(receiverGameExistent);

        final Game senderGame = server.getGameManager().getGame(senderClient);
        assertNotNull(senderGame);

        final Game receiverGame = server.getGameManager().getGame(senderClient);
        assertNotNull(receiverGame);

        assertEquals(senderGame, receiverGame);
    }
}
