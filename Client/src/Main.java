// Ron Ashkenazi 206616666
// Gal Sinai 206846743

import java.io.*;

/**
 * The Main class is the entry point of the program.
 */
public class Main {

    /**
     * The main method initializes the necessary objects for communication, starts a separate thread to read messages
     * from the server, sets up the GUI, and sends messages to the server.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            SocketProxy socketProxy = new SocketProxy("localhost", 1300);
            System.out.println("Connected to the server.");
            ClientGUI gui = new ClientGUI();

            // Create the necessary objects for communication
            DataInputStream reader = socketProxy.getReader();
            DataOutputStream writer = socketProxy.getWriter();

            // Register the SocketProxy as a consumer of the received messages
            socketProxy.addConsumer(gui);

            // Start a separate thread to read messages from the server
            Thread messageReaderThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readUTF()) != null) {
                        if (message.contains("add-username")) {
                            String userName = "add-username";
                            String result = message.replaceAll(userName, "");
                            // Add new user to the chat
                            gui.consumeUserNames(result);
                        } else if (message.contains("remove-username")) {
                            String userName = "remove-username";
                            String result = message.replaceAll(userName, "");
                            // Remove user from the chat
                            gui.consumeRemoveUserNames(result);
                        } else if (message.contains("private-message-to-")) {
                            String[] parts = message.split("--splitter--");
                            String receivingUser = parts[0].replaceAll("private-message-to-", "");
                            String sendingUser = parts[1].replaceAll("from--", "");
                            String messageCut = parts[2];
                            // Handling private message
                            gui.consumePrivate(messageCut, receivingUser, sendingUser);
                        } else {
                            // Forward the message to the GUI and other consumers
                            gui.consume(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageReaderThread.start();

            // Set the PrintWriter object for sending messages
            gui.setWriter(writer);

            // Set the SocketProxy as the server consumer in the GUI
            gui.setServerConsumer(socketProxy);

            // Start the GUI with the SocketProxy
            gui.start(socketProxy);

            // Send messages to the server
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                writer.writeUTF(userInput);
            }

            // Close the socket and exit the program
            socketProxy.disconnect();
            System.out.println("Disconnected from the server.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
