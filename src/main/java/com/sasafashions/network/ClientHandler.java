package com.sasafashions.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles communication between the Sasa Fashions server
 * and one connected network client.
 *
 * <p>Each handler processes one active TCP connection. The
 * handler receives commands, validates their basic structure
 * and delegates approved business-data requests to
 * {@link NetworkDataService}.</p>
 *
 * <p>Because this class implements {@link Runnable}, different
 * clients can be handled by separate threads.</p>
 *
 * <p>Supported commands are:</p>
 *
 * <ul>
 *     <li>{@code PING}</li>
 *     <li>{@code HELLO}</li>
 *     <li>{@code SUMMARY}</li>
 *     <li>{@code CUSTOMER|CUST-0001}</li>
 *     <li>{@code ORDER|ORD-0001}</li>
 *     <li>{@code HELP}</li>
 *     <li>{@code EXIT}</li>
 * </ul>
 *
 * @author SASA Group
 * @version 1.2
 */
public class ClientHandler implements Runnable {

    /**
     * Socket belonging to the connected client.
     */
    private final Socket socket;

    /**
     * Service providing approved access to business data.
     */
    private final NetworkDataService dataService;

    /**
     * Creates a handler for one connected network client.
     *
     * @param socket connected client socket
     */
    public ClientHandler(Socket socket) {

        this.socket = socket;
        this.dataService =
                new NetworkDataService();
    }

    /**
     * Processes messages received from the connected client.
     */
    @Override
    public void run() {

        try (
                BufferedReader input =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        );

                PrintWriter output =
                        new PrintWriter(
                                socket.getOutputStream(),
                                true
                        )
        ) {

            String message;

            while ((message = input.readLine()) != null) {

                System.out.println(
                        "Received from "
                        + socket.getInetAddress()
                                .getHostAddress()
                        + ": "
                        + message
                );

                String response =
                        processCommand(message);

                output.println(response);

                if (message.trim()
                        .equalsIgnoreCase("EXIT")) {

                    break;
                }
            }

        } catch (IOException e) {

            System.err.println(
                    "Client communication error: "
                    + e.getMessage()
            );

        } finally {

            closeSocket();

            System.out.println(
                    "Client disconnected."
            );
        }
    }

    /**
     * Interprets a command and returns its response.
     *
     * @param command command received from the client
     * @return server response
     */
    private String processCommand(String command) {

        if (command == null
                || command.isBlank()) {

            return "ERROR: Command cannot be empty.";
        }

        String cleaned =
                command.trim();

        if (cleaned.equalsIgnoreCase("PING")) {
            return "PONG";
        }

        if (cleaned.equalsIgnoreCase("HELLO")) {

            return "WELCOME TO SASA FASHIONS";
        }

        if (cleaned.equalsIgnoreCase("SUMMARY")) {

            return dataService.getSummary();
        }

        if (cleaned.equalsIgnoreCase("HELP")) {

            return "COMMANDS: "
                    + "PING, "
                    + "HELLO, "
                    + "SUMMARY, "
                    + "CUSTOMER|CUSTOMER_ID, "
                    + "ORDER|ORDER_ID, "
                    + "HELP, "
                    + "EXIT";
        }

        if (cleaned.equalsIgnoreCase("EXIT")) {
            return "GOODBYE";
        }

        String[] parts =
                cleaned.split("\\|", 2);

        String commandName =
                parts[0]
                        .trim()
                        .toUpperCase();

        if (commandName.equals("CUSTOMER")) {

            if (parts.length < 2
                    || parts[1].isBlank()) {

                return "ERROR: Customer ID is required. "
                        + "Example: CUSTOMER|CUST-0001";
            }

            String customerId =
                    parts[1]
                            .trim()
                            .toUpperCase();

            return dataService
                    .findCustomer(customerId);
        }

        if (commandName.equals("ORDER")) {

            if (parts.length < 2
                    || parts[1].isBlank()) {

                return "ERROR: Order ID is required. "
                        + "Example: ORDER|ORD-0001";
            }

            String orderId =
                    parts[1]
                            .trim()
                            .toUpperCase();

            return dataService
                    .findOrder(orderId);
        }

        return "ERROR: Unknown command. "
                + "Type HELP to see available commands.";
    }

    /**
     * Safely closes the client's socket.
     */
    private void closeSocket() {

        try {

            if (socket != null
                    && !socket.isClosed()) {

                socket.close();
            }

        } catch (IOException e) {

            System.err.println(
                    "Error closing client socket: "
                    + e.getMessage()
            );
        }
    }
}