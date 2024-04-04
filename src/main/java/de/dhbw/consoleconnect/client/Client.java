package de.dhbw.consoleconnect.client;

import de.dhbw.consoleconnect.client.file.PropertiesFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final PropertiesFile propertiesFile;
    private final Scanner scanner;
    private final String clientName;

    public Client(final boolean ignoreClientConfiguration) {
        this.propertiesFile = new PropertiesFile();
        this.scanner = new Scanner(System.in);
        if (!ignoreClientConfiguration && this.propertiesFile.getProperties().containsKey("client.name")) {
            this.clientName = this.propertiesFile.getProperties().getProperty("client.name");
        } else {
            System.out.println("Please enter your name: ");
            System.out.print("> ");
            this.clientName = scanner.nextLine();
            System.out.print("(GLOBAL) > ");
            this.propertiesFile.getProperties().put("client.name", this.clientName);
            this.propertiesFile.save();
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

    public String getClientName() {
        return this.clientName;
    }
}
