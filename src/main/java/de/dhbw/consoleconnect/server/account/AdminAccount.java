package de.dhbw.consoleconnect.server.account;

public class AdminAccount extends Account {

    @Override
    public String getName() {
        return "*** Admin ***";
    }

    @Override
    public String getStatus() {
        return "\"It's Over Anakin! I have the high ground!\"";
    }
}
