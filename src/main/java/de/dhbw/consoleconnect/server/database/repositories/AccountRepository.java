package de.dhbw.consoleconnect.server.database.repositories;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.database.Database;

public interface AccountRepository<T> extends Database<T> {

    Account getAccountById(final int accountId);

    Account getAccountByName(final String accountName);

    void saveAccount(final Account account);

    void deleteAccount(final Account account);
}
