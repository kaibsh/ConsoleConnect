package de.dhbw.consoleconnect.client;

import de.dhbw.consoleconnect.client.file.PropertiesFile;
import de.dhbw.consoleconnect.client.hook.HookManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final PropertiesFile propertiesFile;
    private final HookManager hookManager;
    private final Scanner scanner;
    private final String clientName;
    private String roomName = "GLOBAL";

    public Client(final boolean ignoreClientConfiguration) {
        this.propertiesFile = new PropertiesFile();
        this.hookManager = new HookManager(this);
        this.scanner = new Scanner(System.in);
        if (!ignoreClientConfiguration && this.propertiesFile.getProperties().containsKey("client.name")) {
            this.clientName = this.propertiesFile.getProperties().getProperty("client.name");
        } else {
            System.out.println("Please enter your name: ");
            System.out.print("> ");
            this.clientName = scanner.nextLine();
            if (!ignoreClientConfiguration) {
                this.propertiesFile.getProperties().put("client.name", this.clientName);
                this.propertiesFile.save();
            }
        }
    }

    public void connect() {
        try (final Socket socket = new Socket("127.0.0.1", 1234)) {
            System.out.println("[INFO] Successfully started chat-client!");
            System.out.println("[INFO] The client is connected to server: '" + socket.getLocalAddress().getHostAddress() + ":" + socket.getPort() + "'");

            final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            final ClientThread clientThread = new ClientThread(this, socket);
            clientThread.start();

            printWriter.println("[HANDSHAKE] " + this.clientName);

            while (clientThread.isAlive()) {
                final String input = scanner.nextLine();
                if (input != null && !input.isBlank()) {
                    if (input.startsWith("/")) {
                        clientThread.getMessages().add(input);
                    }
                    printWriter.println(input);
                }
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public HookManager getHookManager() {
        return this.hookManager;
    }

    public String getClientName() {
        return this.clientName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(final String roomName) {
        this.roomName = roomName;
    }
}
