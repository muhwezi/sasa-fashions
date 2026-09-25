package com.sasafashions.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Command-line TCP client for the Sasa Fashions
 * Management System.
 *
 * <p>The client sends controlled commands to the
 * Sasa Fashions server and displays the responses.</p>
 *
 * <p>During local testing, {@code localhost} is used.
 * When the system is tested between two computers,
 * {@link #SERVER_IP} can be changed to the LAN IP address
 * of the computer running {@link SasaNetworkServer}.</p>
 *
 * @author SASA Group
 * @version 1.2
 */
public class NetworkClient {

    /**
     * Address of the server computer.
     */
    private static final String SERVER_IP =
            "localhost";

    /**
     * TCP port exposed by the server.
     */
    private static final int SERVER_PORT =
            SasaNetworkServer.PORT;

    /**
     * Starts the Sasa Fashions network client.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(String[] args) {

        System.out.println(
                "======================================"
        );

        System.out.println(
                "      SASA FASHIONS NETWORK CLIENT"
        );

        System.out.println(
                "======================================"
        );

        try (
                Socket socket =
                        new Socket(
                                SERVER_IP,
                                SERVER_PORT
                        );

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
                        );

                Scanner keyboard =
                        new Scanner(System.in)
        ) {

            System.out.println(
                    "Connected successfully to "
                    + SERVER_IP
                    + ":"
                    + SERVER_PORT
            );

            printCommands();

            while (true) {

                System.out.print(
                        "\nEnter command: "
                );

                String command =
                        keyboard.nextLine()
                                .trim();

                if (command.isBlank()) {

                    System.out.println(
                            "Please enter a command."
                    );

                    continue;
                }

                output.println(command);

                String response =
                        input.readLine();

                if (response == null) {

                    System.out.println(
                            "Server closed the connection."
                    );

                    break;
                }

                System.out.println();
                System.out.println(
                        "Server Response:"
                );

                System.out.println(response);

                if (command
                        .equalsIgnoreCase("EXIT")) {

                    break;
                }
            }

        } catch (IOException e) {

            System.err.println();
            System.err.println(
                    "Unable to connect to the "
                    + "Sasa Fashions server."
            );

            System.err.println(
                    "Reason: "
                    + e.getMessage()
            );
        }

        System.out.println();
        System.out.println(
                "Network client closed."
        );
    }

    /**
     * Displays the network commands supported by the server.
     */
    private static void printCommands() {

        System.out.println();
        System.out.println(
                "AVAILABLE COMMANDS"
        );

        System.out.println(
                "------------------"
        );

        System.out.println(
                "PING"
        );

        System.out.println(
                "HELLO"
        );

        System.out.println(
                "SUMMARY"
        );

        System.out.println(
                "CUSTOMER|CUST-0001"
        );

        System.out.println(
                "ORDER|ORD-0001"
        );

        System.out.println(
                "HELP"
        );

        System.out.println(
                "EXIT"
        );
    }
}