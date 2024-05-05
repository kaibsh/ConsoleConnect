package de.dhbw.consoleconnect;

import de.dhbw.consoleconnect.client.Client;
import de.dhbw.consoleconnect.server.Server;
import de.dhbw.consoleconnect.server.SocketServer;

public final class ConsoleConnect {
    private static String COMPONENT = System.getenv("COMPONENT");
    private static boolean IGNORE_CLIENT_CONFIGURATION = Boolean.valueOf(System.getenv("IGNORE_CLIENT_CONFIGURATION"));

    public static void main(final String[] arguments) {
        System.out.println("[INFO] Starting ConsoleConnect application...");
        for (final String argument : arguments) {
            System.out.println("[INFO] Found argument: " + argument);
            if (argument.startsWith("--component=")) {
                COMPONENT = argument.replace("--component=", "");
            } else if (argument.startsWith("--ignore-client-configuration=")) {
                IGNORE_CLIENT_CONFIGURATION = Boolean.valueOf(argument.replace("--ignore-client-configuration=", ""));
            }
        }
        if (COMPONENT != null && !COMPONENT.isEmpty()) {
            if (COMPONENT.equalsIgnoreCase("server")) {
                System.out.println("[INFO] Found ConsoleConnect component: 'server'");
                final Server server = new SocketServer();
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
