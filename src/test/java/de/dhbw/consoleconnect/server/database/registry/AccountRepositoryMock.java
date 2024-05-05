package de.dhbw.consoleconnect.server.database.registry;

import de.dhbw.consoleconnect.server.account.Account;
import de.dhbw.consoleconnect.server.database.repositories.AccountRepository;

import java.util.List;

public final class AccountRepositoryMock implements AccountRepository<List<Account>> {

    private List<Account> accounts;

    @Override
    public void initialize(final List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public Account getAccountById(final int accountId) {
        for (final Account account : this.accounts) {
            if (account.getId() == accountId) {
                return account;
            }
        }
        return null;
    }

    @Override
    public Account getAccountByName(final String accountName) {
        if (accountName != null && !accountName.isBlank()) {
            for (final Account account : this.accounts) {
                if (account.getName().equals(accountName.toLowerCase())) {
                    return account;
                }
            }
        }
        return null;
    }

    @Override
    public void saveAccount(final Account account) {
        if (account != null) {
            this.accounts.add(account);
        }
    }

    @Override
    public void deleteAccount(final Account account) {
        if (account != null) {
            this.accounts.remove(account);
        }
    }
}
