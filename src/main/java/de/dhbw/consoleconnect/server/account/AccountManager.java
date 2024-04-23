package de.dhbw.consoleconnect.server.account;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.database.registry.AccountDatabase;

public class AccountManager {

    private final Server server;
    private final AccountDatabase accountDatabase;

    public AccountManager(final Server server) {
        this.server = server;
        this.accountDatabase = new AccountDatabase();
        this.server.getDatabaseManager().registerDatabase(this.accountDatabase);
    }

    public boolean authenticate(final String name, final String password) {
        if (name != null && !name.isBlank() && password != null && !password.isBlank()) {
            final Account account = this.accountDatabase.selectAccount(name);
            if (account != null) {
                System.out.println("[ACCOUNT] Authenticated account '" + account.getName() + "' with password '" + account.getPassword() + "'.");
                return account.getPassword().equals(password);
            } else {
                final Account newAccount = new Account();
                newAccount.setName(name);
                newAccount.setPassword(password);
                this.accountDatabase.insertAccount(newAccount);
                System.out.println("[ACCOUNT] Created account '" + newAccount.getName() + " with password '" + newAccount.getPassword() + "'.");
                return true;
            }
        }
        return false;
    }

    public boolean changePassword(final String name, final String newPassword) {
        if (name != null && !name.isBlank() && newPassword != null && !newPassword.isBlank()) {
            final Account account = this.accountDatabase.selectAccount(name);
            if (account != null) {
                account.setPassword(newPassword);
                this.accountDatabase.updateAccount(account);
                System.out.println("[ACCOUNT] Changed password '" + newPassword + "' for account '" + account.getName() + "'.");
                return true;
            }
        }
        return false;
    }

    public boolean deleteAccount(final Account account) {
        if (account != null) {
            this.accountDatabase.deleteAccount(account);
            System.out.println("[ACCOUNT] Deleted account '" + account.getName() + "'.");
            return true;
        }
        return false;
    }

    public Account getAccount(final String name) {
        if (name != null && !name.isBlank()) {
            return this.accountDatabase.selectAccount(name);
        }
        return null;
    }
}
