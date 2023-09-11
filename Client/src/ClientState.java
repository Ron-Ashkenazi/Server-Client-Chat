// Ron Ashkenazi 206616666
// Gal Sinai 206846743

/**
 * The ClientState interface represents the state of a client in a network connection.
 * It provides methods for entering the state, connecting to a server, disconnecting from a server,
 * and sending a message.
 */
public interface ClientState {

    /**
     * Enters the current client state.
     */
    void enter();

    /**
     * Connects the client to a server.
     */
    void connect();

    /**
     * Disconnects the client from a server.
     */
    void disconnect();

    /**
     * Sends a message from the client.
     *
     * @param message The message to be sent.
     */
    void sendMessage(String message);
}
