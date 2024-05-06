package de.dhbw.consoleconnect.server.account;

import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.ServerClient;
import de.dhbw.consoleconnect.server.database.repositories.AccountRepository;

public final class AccountManager {

    private final Server server;
    private final AccountRepository accountRepository;

    public AccountManager(final Server server, final AccountRepository<?> accountRepository) {
        this.server = server;
        this.accountRepository = accountRepository;
        this.server.getDatabaseService().registerDatabase(this.accountRepository);
    }

    public boolean authenticate(final String name, final String password) {
        if (name != null && !name.isBlank() && password != null && !password.isBlank()) {
            final Account account = this.accountRepository.getAccountByName(name);
            if (account != null) {
                System.out.println("[ACCOUNT] Authenticated account '" + account.getName() + "' with password '" + account.getPassword() + "'.");
                return account.getPassword().equals(password);
            } else {
                final Account newAccount = new Account();
                newAccount.setName(name);
                newAccount.setPassword(password);
                this.accountRepository.saveAccount(newAccount);
                System.out.println("[ACCOUNT] Created account '" + newAccount.getName() + "' with password '" + newAccount.getPassword() + "'.");
                return true;
            }
        }
        return false;
    }

    public boolean changePassword(final String name, final String newPassword) {
        if (name != null && !name.isBlank() && newPassword != null && !newPassword.isBlank()) {
            final Account account = this.accountRepository.getAccountByName(name);
            if (account != null) {
                account.setPassword(newPassword);
                this.accountRepository.saveAccount(account);
                System.out.println("[ACCOUNT] Changed password '" + newPassword + "' for account '" + account.getName() + "'.");
                return true;
            }
        }
        return false;
    }

    public boolean changeStatus(final String name, final String status) {
        if (name != null && !name.isBlank() && status != null && !status.isBlank()) {
            final Account account = this.accountRepository.getAccountByName(name);
            if (account != null) {
                account.setStatus(status);
                this.accountRepository.saveAccount(account);
                System.out.println("[ACCOUNT] Changed status '" + status + "' for account '" + account.getName() + "'.");
                return true;
            }
        }
        return false;
    }

    public boolean deleteAccount(final Account account) {
        if (account != null) {
            this.accountRepository.deleteAccount(account);
            System.out.println("[ACCOUNT] Deleted account '" + account.getName() + "'.");
            return true;
        }
        return false;
    }

    public void adminMessage(final String message) {
        if (message != null && !message.isBlank()) {
            for (final ServerClient client : this.server.getClients()) {
                client.sendMessage("[*** ADMIN MESSAGE ***]: " + message);
            }
        }
    }

    public Account getAccountById(final int id) {
        return this.accountRepository.getAccountById(id);
    }

    public Account getAccountByName(final String name) {
        if (name != null && !name.isBlank()) {
            return this.accountRepository.getAccountByName(name);
        }
        return null;
    }
}
