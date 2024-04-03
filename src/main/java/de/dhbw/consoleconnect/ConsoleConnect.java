package de.dhbw.consoleconnect;

import de.dhbw.consoleconnect.client.Client;
import de.dhbw.consoleconnect.server.Server;

public class ConsoleConnect {
    private final static String COMPONENT = System.getenv("COMPONENT");
    private final static boolean IGNORE_CLIENT_CONFIGURATION = Boolean.valueOf(System.getenv("IGNORE_CLIENT_CONFIGURATION"));

    public static void main(final String[] arguments) {
        System.out.println("[INFO] Starting ConsoleConnect application...");
        if (COMPONENT != null && !COMPONENT.isEmpty()) {
            if (COMPONENT.equalsIgnoreCase("server")) {
                System.out.println("[INFO] Found ConsoleConnect component: 'server'");
                final Server server = new Server();
                server.start();
            } else if (COMPONENT.equalsIgnoreCase("client")) {
                System.out.println("[INFO] Found ConsoleConnect component: 'client'");
                final Client client = new Client(IGNORE_CLIENT_CONFIGURATION);
                client.connect();
            } else {
                System.out.println("[ERROR] Unknown ConsoleConnect component found: " + COMPONENT);
                System.out.println("[ERROR] Please provide one of the following environment variables: 'COMPONENT=server' or 'COMPONENT=client'");
                System.out.println("[INFO] Stopping ConsoleConnect application...");
                System.out.println("[INFO] Successfully stopped ConsoleConnect application!");
            }
        } else {
            System.out.println("[ERROR] No ConsoleConnect component found!");
            System.out.println("[ERROR] Please provide one of the following environment variables: 'COMPONENT=server' or 'COMPONENT=client'");
            System.out.println("[INFO] Stopping ConsoleConnect application...");
            System.out.println("[INFO] Successfully stopped ConsoleConnect application!");
        }
    }
}
