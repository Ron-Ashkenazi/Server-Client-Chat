// Ron Ashkenazi 206616666
// Gal Sinai 206846743

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * The ClientGUI class represents the graphical user interface for the client application.
 */
public class ClientGUI implements StringConsumer, StringProducer {
    private List<StringConsumer> consumers;
    private JFrame frame;
    private JTextField textField;
    private JButton sendButton, btConnect, btDisconnect, btUserName;
    private JTextField userName;
    private JPanel panelSouth, panelCenter, panelNorth;
    private ClientState currentState;
    private DataOutputStream writer;
    private boolean isConnected = false;
    private JTextArea chatArea;
    private String setUserName;
    private JComboBox usersList;
    private StringConsumer serverConsumer;

    /**
     * Constructs a new ClientGUI object.
     */
    public ClientGUI() {
        this.consumers = new ArrayList<>();
        frame = new JFrame();
        textField = new JTextField(35);
        userName = new JTextField(20);
        sendButton = new JButton("Send");
        btConnect = new JButton("Connect");
        btDisconnect = new JButton("Disconnect");
        btUserName = new JButton("Set UserName");
        panelSouth = new JPanel();
        panelCenter = new JPanel();
        panelNorth = new JPanel();
        currentState = new DisconnectedState();
        chatArea = new JTextArea();
        usersList = new JComboBox(consumers.toArray());
        usersList.addItem("All");
    }

    /**
     * Adding Consumer
     */
    @Override
    public void addConsumer(StringConsumer stringConsumer) {
        consumers.add(stringConsumer);
    }

    /**
     * Removing Consumer
     */
    @Override
    public void removeConsumer(StringConsumer stringConsumer) {
        consumers.remove(stringConsumer);
    }

    /**
     * Checks if a user exists in the given JComboBox.
     *
     * @param arr The JComboBox to check.
     * @param str The username to search for.
     * @return true if the user exists, false otherwise.
     */
    public boolean userExistsInComboBox(JComboBox arr, String str) {
        int itemCount = arr.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (str.equals(arr.getItemAt(i).toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle the received message in the GUI
     */
    @Override
    public void consume(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isConnected) {
                    chatArea.append(message + "\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                }
            }
        });
    }

    /**
     * Handles the received private message in the GUI.
     *
     * @param message       The private message.
     * @param receivingUser The user receiving the message.
     * @param sendingUser   The user sending the message.
     */
    public void consumePrivate(String message, String receivingUser, String sendingUser) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isConnected && receivingUser.equals(setUserName)) {
                    chatArea.append("(Private from): " + message + "\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                } else if (isConnected && sendingUser.equals(setUserName)) {
                    chatArea.append("(Private to: " + receivingUser + ") " + message + "\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                }
            }
        });
    }

    /**
     * Adds a user to the GUI.
     *
     * @param str The username to add.
     */
    public void consumeUserNames(String str) {
        boolean exists = userExistsInComboBox(usersList, str);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isConnected && !str.equals(setUserName) && !exists) {
                    usersList.addItem(str);
                }
            }
        });
    }

    /**
     * Removes a user from the GUI.
     *
     * @param str The username to remove.
     */
    public void consumeRemoveUserNames(String str) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isConnected) {
                    usersList.removeItem(str);
                }
            }
        });
    }

    /**
     * Set the server consumer
     */
    public void setServerConsumer(StringConsumer serverConsumer) {
        this.serverConsumer = serverConsumer;
    }

    /**
     * Starts the chat application using the given socket proxy.
     *
     * @param socketProxy The socket proxy to use for communication.
     */
    public void start(SocketProxy socketProxy) {
        // Set up the layout and background colors of the panels
        frame.setLayout(new BorderLayout());
        panelNorth.setBackground(Color.YELLOW);
        panelCenter.setBackground(Color.WHITE);
        panelSouth.setBackground(Color.cyan);

        // Set the layout and add components to the south panel
        panelSouth.setLayout(new FlowLayout());
        panelSouth.add(usersList);
        panelSouth.add(textField);
        panelSouth.add(sendButton);
        panelSouth.add(userName);
        panelSouth.add(btUserName);

        // Add components to the north panel
        panelNorth.add(btConnect);
        panelNorth.add(btDisconnect);
        btDisconnect.setEnabled(false);

        // Add panels to the frame
        frame.add(panelNorth, BorderLayout.NORTH);
        frame.add(panelCenter, BorderLayout.CENTER);
        frame.add(panelSouth, BorderLayout.SOUTH);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.setSize(800, 500);
        frame.setVisible(true);

        // ActionListener for the connect button
        btConnect.addActionListener(new ActionListener() {
            /**
             * Called when the connect button is clicked.
             *
             * @param e The action event.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the username and perform necessary actions
                setUserName = userName.getText().trim();
                if (!setUserName.equals("") && !btUserName.isEnabled()) {
                    socketProxy.connect();
                    currentState.connect();
                    isConnected = true;
                    try {
                        sendUserName(setUserName, socketProxy);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        // ActionListener for the disconnect button
        btDisconnect.addActionListener(new ActionListener() {
            /**
             * Called when the disconnect button is clicked.
             *
             * @param e The action event.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Disconnect and reset the state
                socketProxy.disconnect();
                currentState.disconnect();
                isConnected = false;
                btUserName.setEnabled(true);
                usersList.removeAllItems();
                usersList.addItem("All");
                try {
                    removeUserName(setUserName, socketProxy);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // ActionListener for the send button
        sendButton.addActionListener(new ActionListener() {
            /**
             * Called when the send button is clicked.
             *
             * @param e The action event.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Send the message
                try {
                    sendMessage(setUserName + ": " + textField.getText(), socketProxy);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // ActionListener for the username button
        btUserName.addActionListener(new ActionListener() {
            /**
             * Called when the username button is clicked.
             *
             * @param e The action event.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the username and enable/disable the button accordingly
                setUserName = userName.getText().trim();
                if (!setUserName.equals("")) {
                    btUserName.setEnabled(false);
                } else {
                    btUserName.setEnabled(true);
                }
            }
        });
    }

    /**
     * Sends a message to the server.
     *
     * @param message     The message to send.
     * @param socketProxy The socket proxy used for communication.
     * @throws IOException if an I/O error occurs.
     */
    private void sendMessage(String message, SocketProxy socketProxy) throws IOException {
        if (isConnected && setUserName != null) {
            String selectedUser = usersList.getSelectedItem().toString();
            if (!selectedUser.equals("All")) {
                writer.writeUTF("private-message-to-" + selectedUser + "--splitter--" +
                        "from--" + setUserName + "--splitter--" + message);
            } else {
                writer.writeUTF(message);
            }
            textField.setText("");
        } else {
            System.out.println("The client is disconnected sendMessage");
        }
    }

    /**
     * Sends the username to the server.
     *
     * @param userName    The username to send.
     * @param socketProxy The socket proxy used for communication.
     * @throws IOException if an I/O error occurs.
     */
    private void sendUserName(String userName, SocketProxy socketProxy) throws IOException {
        if (isConnected && setUserName != null) {
            userName = "add-username" + userName;
            writer.writeUTF(userName);
        } else {
            System.out.println("The client is disconnected sendUserName");
        }
    }

    /**
     * Removes the username from the server.
     *
     * @param userName    The username to remove.
     * @param socketProxy The socket proxy used for communication.
     * @throws IOException if an I/O error occurs.
     */
    private void removeUserName(String userName, SocketProxy socketProxy) throws IOException {
        userName = "remove-username" + userName;
        writer.writeUTF(userName);
    }

    /**
     * Sets the writer for the client.
     *
     * @param writer The writer to set.
     */
    public void setWriter(DataOutputStream writer) {
        this.writer = writer;
    }

    /**
     * Represents the state of a connected client.
     */
    private class ConnectedState implements ClientState {
        @Override
        public void enter() {
            // Disable connect button
            btConnect.setEnabled(false);
            // Enable disconnect button
            btDisconnect.setEnabled(true);
        }

        @Override
        public void connect() {
            // Already connected
        }

        @Override
        public void disconnect() {
            // Transition to disconnected state
            currentState = new DisconnectedState();
            currentState.enter();
        }

        @Override
        public void sendMessage(String message) {
            // Handle sending message
            System.out.println("Sending message: " + message);
        }
    }

    /**
     * Represents the state of a disconnected client.
     */
    private class DisconnectedState implements ClientState {
        @Override
        public void enter() {
            // Enable connect button
            btConnect.setEnabled(true);
            // Disable disconnect button
            btDisconnect.setEnabled(false);
        }

        @Override
        public void connect() {
            // Transition to connected state
            currentState = new ConnectedState();
            currentState.enter();
        }

        @Override
        public void disconnect() {
            // Already disconnected
        }

        @Override
        public void sendMessage(String message) {
            // Client is disconnected, cannot send message
            System.out.println("Cannot send message. Client is disconnected.");
        }
    }
}
