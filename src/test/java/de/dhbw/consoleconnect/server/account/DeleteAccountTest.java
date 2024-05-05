package de.dhbw.consoleconnect.server.account;

import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.ServerClientMock;
import de.dhbw.consoleconnect.server.ServerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteAccountTest {

    @Test
    public void testDeleteAccount() {
        final ServerMock server = new ServerMock();
        final ServerClient client = new ServerClientMock(server, "DeleteAccountTest");

        server.getAccountManager().authenticate("DeleteAccountTest", "password");
        final Account account = server.getAccountManager().getAccountByName("DeleteAccountTest");

        final boolean deleted = server.getAccountManager().deleteAccount(account);
        assertTrue(deleted);

        final Account deletedAccount = server.getAccountManager().getAccountByName("DeleteAccountTest");
        assertNull(deletedAccount);
    }
}
