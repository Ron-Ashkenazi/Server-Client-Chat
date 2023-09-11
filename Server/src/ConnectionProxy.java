// Ron Ashkenazi 206616666
// Gal Sinai 206846743

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The ConnectionProxy class represents a thread that acts as a proxy between a client and a server.
 * It handles the communication with the client and relays messages to a MessageBoard.
 */
public class ConnectionProxy extends Thread implements StringConsumer, StringProducer {

    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private List<StringConsumer> consumers;
    private Socket socket = null;
    private MessageBoard messageBoard;

    /**
     * Constructs a ConnectionProxy object with the specified socket and message board.
     *
     * @param socket       the socket connected to the client
     * @param messageBoard the message board to relay messages
     * @throws ChatException if there is a problem creating the stream
     */
    public ConnectionProxy(Socket socket, MessageBoard messageBoard) throws ChatException {
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);
            this.consumers = new ArrayList<>();
            this.messageBoard = messageBoard;
        } catch (IOException e) {
            throw new ChatException("Problem creating stream.", e);
        }
    }

    /**
     * Adds a consumer to the list of consumers.
     *
     * @param stringConsumer the StringConsumer to add
     */
    @Override
    public void addConsumer(StringConsumer stringConsumer) {
        consumers.add(stringConsumer);
    }

    /**
     * Removes a consumer from the list of consumers.
     *
     * @param stringConsumer the StringConsumer to remove
     */
    @Override
    public void removeConsumer(StringConsumer stringConsumer) {
        consumers.remove(stringConsumer);
    }

    /**
     * Consumes a string by writing it to the output stream.
     *
     * @param message the string to consume
     * @throws ChatException if there is an I/O error
     */
    @Override
    public void consume(String message) throws ChatException {
        try {
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            throw new ChatException("Failed to write string to output stream.", e);
        }
    }

    /**
     * Starts the thread and listens for messages from the client.
     */
    @Override
    public void run() {
        try {
            while (true) {
                // Read the incoming message from the client
                String received = dataInputStream.readUTF();

                // Check if the message is for adding a username
                if (received.contains("add-username")) {
                    String userName = "add-username";
                    String result = received.replaceAll(userName, "");

                    // Add the username to the message board
                    messageBoard.addUser(result);

                    // Broadcast the updated username list to all connected users
                    if (messageBoard.getUserNamesSize() > 0) {
                        for (int i = 0; i < messageBoard.getUserNamesSize(); i++) {
                            String userNameAdd = "add-username";
                            String resultAdd = userNameAdd + messageBoard.getUserNamesIndex(i);
                            messageBoard.broadcast(resultAdd);
                        }
                    }
                }
                // Check if the message is for removing a username
                else if (received.contains("remove-username")) {
                    // Broadcast the message to all connected users
                    messageBoard.broadcast(received);

                    String userName = "remove-username";
                    String result = received.replaceAll(userName, "");

                    // Remove the username from the message board
                    messageBoard.removeUser(result);

                    // Broadcast the updated username list to all connected users
                    if (messageBoard.getUserNamesSize() > 0) {
                        for (int i = 0; i < messageBoard.getUserNamesSize(); i++) {
                            String userNameRemove = "add-username";
                            String resultRemove = userNameRemove + messageBoard.getUserNamesIndex(i);
                            messageBoard.broadcast(resultRemove);
                        }
                    }
                }
                // Otherwise, it's a regular message to be broadcast
                else {
                    messageBoard.broadcast(received);
                }
            }
        } catch (IOException e) {
            // Handle any I/O error that occurs
            e.printStackTrace();
        } catch (ChatException e) {
            // Throw a runtime exception if there's a ChatException
            throw new RuntimeException(e);
        }
    }
}