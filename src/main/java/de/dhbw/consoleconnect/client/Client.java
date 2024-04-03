package de.dhbw.consoleconnect.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Scanner scanner;
    private final String clientName;

    public Client() {
        this.scanner = new Scanner(System.in);
        System.out.println("Please enter your name: ");
        this.clientName = scanner.nextLine();
    }

    public void connect() {
        try (final Socket socket = new Socket("127.0.0.1", 1234)) {
            System.out.println("[INFO] Successfully started chat-client!");
            System.out.println("[INFO] The client is connected to server: '" + socket.getLocalAddress().getHostAddress() + ":" + socket.getPort() + "'");

            final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            final ClientThread clientThread = new ClientThread(this, socket);
            clientThread.start();

            printWriter.println("[HANDSHAKE] <-> " + this.clientName);

            while (clientThread.isAlive()) {
                printWriter.println(scanner.nextLine());
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public String getClientName() {
        return this.clientName;
    }
}
