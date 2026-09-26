package com.sasafashions.network;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Graphical TCP client for the Sasa Fashions Management System.
 *
 * <p>This Swing frame allows a user to connect to the
 * {@link SasaNetworkServer}, send approved commands and view
 * responses returned by the server.</p>
 *
 * <p>The interface supports general network tests such as
 * {@code PING} and {@code HELLO}, as well as database-backed
 * requests such as customer lookup, order lookup and system
 * summary generation.</p>
 *
 * <p>Networking operations are executed outside the Swing
 * Event Dispatch Thread using {@link SwingWorker}. This prevents
 * the user interface from freezing while waiting for the server.</p>
 *
 * @author SASA Group
 * @version 1.3
 */
public class NetworkClientFrame extends JFrame {

    /**
     * Default TCP port used by the Sasa Fashions server.
     */
    private static final int DEFAULT_PORT =
            SasaNetworkServer.PORT;

    /**
     * Maximum time allowed when establishing a connection.
     */
    private static final int CONNECTION_TIMEOUT =
            5000;

    private JTextField txtServerIp;
    private JTextField txtCustomerId;
    private JTextField txtOrderId;
    private JTextField txtCommand;

    private JTextArea txtOutput;

    private JButton btnConnect;
    private JButton btnDisconnect;
    private JButton btnPing;
    private JButton btnHello;
    private JButton btnSummary;
    private JButton btnCustomer;
    private JButton btnOrder;
    private JButton btnHelp;
    private JButton btnSend;
    private JButton btnExit;

    /**
     * TCP socket connected to the server.
     */
    private Socket socket;

    /**
     * Reads responses received from the server.
     */
    private BufferedReader input;

    /**
     * Sends commands to the server.
     */
    private PrintWriter output;

    /**
     * Creates the Sasa Fashions network client window.
     */
    public NetworkClientFrame() {

        initializeFrame();
        initializeComponents();
        buildInterface();
        registerEvents();
        updateConnectionState(false);
    }

    /**
     * Configures the principal properties of the Swing frame.
     */
    private void initializeFrame() {

        setTitle(
                "Sasa Fashions - Network Client"
        );

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setSize(
                850,
                650
        );

        setMinimumSize(
                new Dimension(
                        760,
                        580
                )
        );

        setLocationRelativeTo(null);
    }

    /**
     * Creates the Swing controls used by the networking screen.
     */
    private void initializeComponents() {

        txtServerIp =
                new JTextField(
                        "localhost",
                        15
                );

        txtCustomerId =
                new JTextField(
                        "CUST-0001",
                        12
                );

        txtOrderId =
                new JTextField(
                        "ORD-0001",
                        12
                );

        txtCommand =
                new JTextField();

        txtOutput =
                new JTextArea();

        txtOutput.setEditable(false);

        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);

        txtOutput.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );

        btnConnect =
                new JButton(
                        "Connect"
                );

        btnDisconnect =
                new JButton(
                        "Disconnect"
                );

        btnPing =
                new JButton(
                        "Ping"
                );

        btnHello =
                new JButton(
                        "Hello"
                );

        btnSummary =
                new JButton(
                        "System Summary"
                );

        btnCustomer =
                new JButton(
                        "Find Customer"
                );

        btnOrder =
                new JButton(
                        "Find Order"
                );

        btnHelp =
                new JButton(
                        "Help"
                );

        btnSend =
                new JButton(
                        "Send Command"
                );

        btnExit =
                new JButton(
                        "Exit"
                );
    }

    /**
     * Builds the visible layout of the networking interface.
     */
    private void buildInterface() {

        JPanel root =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        root.setBorder(
                new EmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        setContentPane(root);

        /*
         * HEADER
         */
        JPanel headerPanel =
                new JPanel();

        headerPanel.setLayout(
                new BoxLayout(
                        headerPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblTitle =
                new JLabel(
                        "SASA FASHIONS NETWORK CLIENT"
                );

        lblTitle.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        22
                )
        );

        lblTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel lblSubtitle =
                new JLabel(
                        "TCP Client-Server Communication"
                );

        lblSubtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        headerPanel.add(lblTitle);

        headerPanel.add(
                Box.createVerticalStrut(5)
        );

        headerPanel.add(lblSubtitle);

        root.add(
                headerPanel,
                BorderLayout.NORTH
        );

        /*
         * MAIN BODY
         */
        JPanel bodyPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        root.add(
                bodyPanel,
                BorderLayout.CENTER
        );

        JPanel controlsPanel =
                new JPanel();

        controlsPanel.setLayout(
                new BoxLayout(
                        controlsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        controlsPanel.add(
                createConnectionPanel()
        );

        controlsPanel.add(
                Box.createVerticalStrut(10)
        );

        controlsPanel.add(
                createCommandPanel()
        );

        controlsPanel.add(
                Box.createVerticalStrut(10)
        );

        controlsPanel.add(
                createLookupPanel()
        );

        controlsPanel.add(
                Box.createVerticalStrut(10)
        );

        controlsPanel.add(
                createManualCommandPanel()
        );

        bodyPanel.add(
                controlsPanel,
                BorderLayout.NORTH
        );

        JScrollPane outputScroll =
                new JScrollPane(
                        txtOutput
                );

        outputScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Network Communication Log"
                )
        );

        bodyPanel.add(
                outputScroll,
                BorderLayout.CENTER
        );

        /*
         * FOOTER
         */
        JPanel footer =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        footer.add(btnExit);

        root.add(
                footer,
                BorderLayout.SOUTH
        );
    }

    /**
     * Creates the server connection controls.
     *
     * @return connection panel
     */
    private JPanel createConnectionPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Server Connection"
                )
        );

        panel.add(
                new JLabel(
                        "Server IP:"
                )
        );

        panel.add(txtServerIp);

        panel.add(
                new JLabel(
                        "Port:"
                )
        );

        JLabel lblPort =
                new JLabel(
                        String.valueOf(
                                DEFAULT_PORT
                        )
                );

        panel.add(lblPort);

        panel.add(btnConnect);
        panel.add(btnDisconnect);

        return panel;
    }

    /**
     * Creates buttons for general network commands.
     *
     * @return general command panel
     */
    private JPanel createCommandPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "General Commands"
                )
        );

        panel.add(btnPing);
        panel.add(btnHello);
        panel.add(btnSummary);
        panel.add(btnHelp);

        return panel;
    }

    /**
     * Creates controls for customer and order database lookups.
     *
     * @return database lookup panel
     */
    private JPanel createLookupPanel() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                2,
                                1,
                                5,
                                5
                        )
                );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Database Requests"
                )
        );

        JPanel customerPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        customerPanel.add(
                new JLabel(
                        "Customer ID:"
                )
        );

        customerPanel.add(
                txtCustomerId
        );

        customerPanel.add(
                btnCustomer
        );

        JPanel orderPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        orderPanel.add(
                new JLabel(
                        "Order ID:"
                )
        );

        orderPanel.add(
                txtOrderId
        );

        orderPanel.add(
                btnOrder
        );

        panel.add(customerPanel);
        panel.add(orderPanel);

        return panel;
    }

    /**
     * Creates the manual command entry section.
     *
     * @return manual command panel
     */
    private JPanel createManualCommandPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                5,
                                5
                        )
                );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Manual Command"
                )
        );

        panel.add(
                txtCommand,
                BorderLayout.CENTER
        );

        panel.add(
                btnSend,
                BorderLayout.EAST
        );

        return panel;
    }

    /**
     * Registers event listeners for all interactive controls.
     */
    private void registerEvents() {

        btnConnect.addActionListener(
                event -> connectToServer()
        );

        btnDisconnect.addActionListener(
                event -> disconnectFromServer()
        );

        btnPing.addActionListener(
                event -> sendCommand("PING")
        );

        btnHello.addActionListener(
                event -> sendCommand("HELLO")
        );

        btnSummary.addActionListener(
                event -> sendCommand("SUMMARY")
        );

        btnHelp.addActionListener(
                event -> sendCommand("HELP")
        );

        btnCustomer.addActionListener(
                event -> sendCustomerRequest()
        );

        btnOrder.addActionListener(
                event -> sendOrderRequest()
        );

        btnSend.addActionListener(
                event -> sendManualCommand()
        );

        txtCommand.addActionListener(
                event -> sendManualCommand()
        );

        btnExit.addActionListener(
                event -> closeFrame()
        );

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent event
                    ) {

                        disconnectSilently();
                    }
                }
        );
    }

    /**
     * Establishes a TCP connection to the specified server.
     *
     * <p>The connection attempt runs in a background worker so
     * that the Swing interface remains responsive.</p>
     */
    private void connectToServer() {

        if (isConnected()) {

            showMessage(
                    "The client is already connected."
            );

            return;
        }

        String serverIp =
                txtServerIp
                        .getText()
                        .trim();

        if (serverIp.isBlank()) {

            showMessage(
                    "Enter the server IP address."
            );

            txtServerIp.requestFocus();

            return;
        }

        btnConnect.setEnabled(false);

        appendLog(
                "Connecting to "
                + serverIp
                + ":"
                + DEFAULT_PORT
                + " ..."
        );

        SwingWorker<Void, Void> worker =
                new SwingWorker<>() {

                    private Exception failure;

                    @Override
                    protected Void doInBackground() {

                        try {

                            Socket newSocket =
                                    new Socket();

                            newSocket.connect(
                                    new InetSocketAddress(
                                            serverIp,
                                            DEFAULT_PORT
                                    ),
                                    CONNECTION_TIMEOUT
                            );

                            BufferedReader newInput =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    newSocket
                                                            .getInputStream()
                                            )
                                    );

                            PrintWriter newOutput =
                                    new PrintWriter(
                                            newSocket
                                                    .getOutputStream(),
                                            true
                                    );

                            socket = newSocket;
                            input = newInput;
                            output = newOutput;

                        } catch (Exception e) {

                            failure = e;
                        }

                        return null;
                    }

                    @Override
                    protected void done() {

                        if (failure == null
                                && isConnected()) {

                            appendLog(
                                    "Connected successfully."
                            );

                            updateConnectionState(
                                    true
                            );

                        } else {

                            appendLog(
                                    "Connection failed: "
                                    + (
                                            failure == null
                                            ? "Unknown error"
                                            : failure.getMessage()
                                    )
                            );

                            disconnectSilently();

                            updateConnectionState(
                                    false
                            );
                        }
                    }
                };

        worker.execute();
    }

    /**
     * Sends the customer lookup command.
     */
    private void sendCustomerRequest() {

        String customerId =
                txtCustomerId
                        .getText()
                        .trim();

        if (customerId.isBlank()) {

            showMessage(
                    "Enter a customer ID."
            );

            txtCustomerId.requestFocus();

            return;
        }

        sendCommand(
                "CUSTOMER|"
                + customerId
                        .toUpperCase()
        );
    }

    /**
     * Sends the order lookup command.
     */
    private void sendOrderRequest() {

        String orderId =
                txtOrderId
                        .getText()
                        .trim();

        if (orderId.isBlank()) {

            showMessage(
                    "Enter an order ID."
            );

            txtOrderId.requestFocus();

            return;
        }

        sendCommand(
                "ORDER|"
                + orderId
                        .toUpperCase()
        );
    }

    /**
     * Sends a command entered manually by the user.
     */
    private void sendManualCommand() {

        String command =
                txtCommand
                        .getText()
                        .trim();

        if (command.isBlank()) {

            showMessage(
                    "Enter a command."
            );

            return;
        }

        sendCommand(command);

        txtCommand.selectAll();
    }

    /**
     * Sends one command to the server and displays the response.
     *
     * @param command command to transmit
     */
    private void sendCommand(
            String command
    ) {

        if (!isConnected()) {

            showMessage(
                    "Connect to the server first."
            );

            return;
        }

        appendLog(
                "CLIENT > "
                + command
        );

        setCommandButtonsEnabled(false);

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    private Exception failure;

                    @Override
                    protected String doInBackground() {

                        try {

                            output.println(command);

                            if (output.checkError()) {

                                throw new IOException(
                                        "Unable to send command."
                                );
                            }

                            return input.readLine();

                        } catch (Exception e) {

                            failure = e;

                            return null;
                        }
                    }

                    @Override
                    protected void done() {

                        try {

                            if (failure != null) {

                                appendLog(
                                        "ERROR > "
                                        + failure.getMessage()
                                );

                                disconnectSilently();

                                updateConnectionState(
                                        false
                                );

                                return;
                            }

                            String response =
                                    get();

                            if (response == null) {

                                appendLog(
                                        "SERVER closed the connection."
                                );

                                disconnectSilently();

                                updateConnectionState(
                                        false
                                );

                                return;
                            }

                            appendLog(
                                    "SERVER > "
                                    + response
                            );

                            if (command
                                    .equalsIgnoreCase(
                                            "EXIT"
                                    )) {

                                disconnectSilently();

                                updateConnectionState(
                                        false
                                );
                            }

                        } catch (Exception e) {

                            appendLog(
                                    "ERROR > "
                                    + e.getMessage()
                            );

                        } finally {

                            if (isConnected()) {

                                setCommandButtonsEnabled(
                                        true
                                );
                            }
                        }
                    }
                };

        worker.execute();
    }

    /**
     * Disconnects from the server after informing it with
     * the {@code EXIT} command.
     */
    private void disconnectFromServer() {

        if (!isConnected()) {

            updateConnectionState(false);

            return;
        }

        try {

            output.println("EXIT");

            String response =
                    input.readLine();

            if (response != null) {

                appendLog(
                        "SERVER > "
                        + response
                );
            }

        } catch (IOException e) {

            appendLog(
                    "Disconnect warning: "
                    + e.getMessage()
            );

        } finally {

            disconnectSilently();

            updateConnectionState(false);

            appendLog(
                    "Disconnected from server."
            );
        }
    }

    /**
     * Closes all network resources without displaying dialogs.
     */
    private void disconnectSilently() {

        try {

            if (input != null) {
                input.close();
            }

        } catch (IOException e) {
            // Resource is already unavailable.
        }

        if (output != null) {
            output.close();
        }

        try {

            if (socket != null
                    && !socket.isClosed()) {

                socket.close();
            }

        } catch (IOException e) {
            // Socket is already unavailable.
        }

        input = null;
        output = null;
        socket = null;
    }

    /**
     * Determines whether a usable server connection exists.
     *
     * @return {@code true} when connected
     */
    private boolean isConnected() {

        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }

    /**
     * Updates controls according to the connection state.
     *
     * @param connected whether the client is connected
     */
    private void updateConnectionState(
            boolean connected
    ) {

        btnConnect.setEnabled(
                !connected
        );

        btnDisconnect.setEnabled(
                connected
        );

        txtServerIp.setEnabled(
                !connected
        );

        setCommandButtonsEnabled(
                connected
        );
    }

    /**
     * Enables or disables controls that send commands.
     *
     * @param enabled desired control state
     */
    private void setCommandButtonsEnabled(
            boolean enabled
    ) {

        btnPing.setEnabled(enabled);
        btnHello.setEnabled(enabled);
        btnSummary.setEnabled(enabled);
        btnCustomer.setEnabled(enabled);
        btnOrder.setEnabled(enabled);
        btnHelp.setEnabled(enabled);
        btnSend.setEnabled(enabled);

        txtCustomerId.setEnabled(enabled);
        txtOrderId.setEnabled(enabled);
        txtCommand.setEnabled(enabled);
    }

    /**
     * Adds a timestamp-free entry to the communication log.
     *
     * @param message message to display
     */
    private void appendLog(
            String message
    ) {

        txtOutput.append(
                message
                + System.lineSeparator()
        );

        txtOutput.setCaretPosition(
                txtOutput
                        .getDocument()
                        .getLength()
        );
    }

    /**
     * Displays a standard information dialog.
     *
     * @param message message to display
     */
    private void showMessage(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Sasa Fashions",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Closes the networking frame and releases resources.
     */
    private void closeFrame() {

        disconnectSilently();

        dispose();
    }

    /**
     * Launches the graphical Sasa Fashions network client.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    try {

                        UIManager.setLookAndFeel(
                                UIManager
                                        .getSystemLookAndFeelClassName()
                        );

                    } catch (
                            ClassNotFoundException
                            | InstantiationException
                            | IllegalAccessException
                            | UnsupportedLookAndFeelException e
                    ) {

                        System.err.println(
                                "Unable to apply system look and feel: "
                                + e.getMessage()
                        );
                    }

                    new NetworkClientFrame()
                            .setVisible(true);
                }
        );
    }
}