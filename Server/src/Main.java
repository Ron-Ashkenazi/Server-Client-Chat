// Ron Ashkenazi 206616666
// Gal Sinai 206846743

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Main class represents the entry point of the server application.
 */
public class Main {
    /**
     * The main method starts the server, accepts client connections,
     * and manages the communication with the connected clients.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Create the server socket and initialize the message board
        ServerSocket server = null;
        MessageBoard mb = new MessageBoard();

        try {
            server = new ServerSocket(1300);
            System.out.println("Server started. Listening for client connections...");

            while (true) {
                // Accept a client connection
                Socket socket = server.accept();

                // Create a connection proxy to handle communication with the client
                ConnectionProxy connection = new ConnectionProxy(socket, mb);
                connection.addConsumer(mb);
                mb.addConsumer(connection);

                // Start the connection proxy
                connection.start();
                System.out.println("Client connected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ChatException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the server socket when done
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
