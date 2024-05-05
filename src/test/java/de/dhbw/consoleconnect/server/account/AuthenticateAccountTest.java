package de.dhbw.consoleconnect.server.account;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticateAccountTest {

    @Test
    public void testAuthenticateAccount() {
        final ServerMock server = new ServerMock();
        final ServerClient client = new ServerClientMock(server, "AuthenticateAccountTest");

        final boolean foundOrCreated = server.getAccountManager().authenticate("AuthenticateAccountTest", "password");
        assertTrue(foundOrCreated);

        final Account account = server.getAccountManager().getAccountByName("AuthenticateAccountTest");
        assertNotNull(account);
    }
}
