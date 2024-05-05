package de.dhbw.consoleconnect.server.game;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartGameRequestTest {

    @Test
    public void testStartGameRequest() {
        final ServerMock server = new ServerMock();
        final ServerClient senderClient = new ServerClientMock(server, "StartGameRequestTest-Sender");
        final ServerClient receiverClient = new ServerClientMock(server, "StartGameRequestTest-Receiver");

        server.getGameManager().startGameRequest(GameMode.ROCK_PAPER_SCISSOR, senderClient, receiverClient);

        final boolean sendedGameRequestExistent = server.getGameManager().isSendedGameRequestExistent(senderClient, receiverClient);
        assertTrue(sendedGameRequestExistent);

        final boolean receivedGameRequestExistent = server.getGameManager().isReceivedGameRequestExistent(receiverClient, senderClient);
        assertTrue(receivedGameRequestExistent);
    }
}
