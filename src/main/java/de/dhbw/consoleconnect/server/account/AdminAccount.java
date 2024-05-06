package de.dhbw.consoleconnect.server.account;

public final class AdminAccount extends Account {

    private final AccountManager accountManager;

    public AdminAccount(final AccountManager accountManger) {
        this.accountManager = accountManger;
    }

    public void broadcastMessage(final String message) {
        this.accountManager.adminMessage(message);
    }

    @Override
    public String getName() {
        return "*** Admin ***";
    }

    @Override
    public String getStatus() {
        return "\"It's Over Anakin! I have the high ground!\"";
    }
}
