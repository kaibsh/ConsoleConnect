package de.dhbw.consoleconnect.server.account;

public class Account {

    private int id;
    private String name;
    private String password;
    private String status;

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        if (id > 0) {
            this.id = id;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.toLowerCase();
        }
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        if (status != null && !status.isBlank()) {
            this.status = status;
        }
    }
}
