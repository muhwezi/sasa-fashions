package com.sasafashions.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Starts the TCP network server for the Sasa Fashions Management System.
 *
 * <p>The server listens for incoming client connections on port 5050.
 * Whenever a client connects, a new {@link ClientHandler} object is created
 * and executed in a separate thread. This allows the server to communicate
 * with more than one client without forcing every client to wait for the
 * previous connection to finish.</p>
 *
 * <p>This class currently provides the basic networking foundation.
 * Database and DAO integration will be added after basic client-server
 * communication has been tested successfully.</p>
 *
 * @author SASA Group
 * @version 1.0
 */
public class SasaNetworkServer {

    /**
     * TCP port used by the Sasa Fashions network service.
     */
    public static final int PORT = 5050;

    /**
     * Starts the Sasa Fashions network server.
     *
     * <p>The method creates a {@link ServerSocket}, waits for client
     * connections and assigns every accepted connection to a
     * {@link ClientHandler} running in its own thread.</p>
     *
     * @param args command-line arguments; currently not used
     */
    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("      SASA FASHIONS NETWORK SERVER");
        System.out.println("======================================");
        System.out.println("Starting server on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server started successfully.");
            System.out.println("Waiting for clients...");
            System.out.println();

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println(
                        "Client connected from: "
                        + clientSocket.getInetAddress().getHostAddress()
                );

                ClientHandler handler =
                        new ClientHandler(clientSocket);

                Thread clientThread =
                        new Thread(handler);

                clientThread.start();
            }

        } catch (IOException e) {

            System.err.println(
                    "Server error: "
                    + e.getMessage()
            );
        }
    }
}