package de.dhbw.consoleconnect.server.database.repositories;

import de.dhbw.consoleconnect.server.account.Account;

public interface AccountRepository {

    Account getAccountById(final int accountId);

    Account getAccountByName(final String accountName);

    void saveAccount(final Account account);

    void deleteAccount(final Account account);
}
