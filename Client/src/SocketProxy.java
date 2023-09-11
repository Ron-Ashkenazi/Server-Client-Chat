// Ron Ashkenazi 206616666
// Gal Sinai 206846743

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The SocketProxy class represents a proxy for a socket connection.
 * It implements the StringConsumer and StringProducer interfaces.
 */
public class SocketProxy implements StringConsumer, StringProducer {
    private Socket socket;
    private List<StringConsumer> consumers;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    /**
     * Constructs a SocketProxy object with the specified host and port.
     *
     * @param host the host name or IP address
     * @param port the port number
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public SocketProxy(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
        this.consumers = new ArrayList<>();
    }

    /**
     * Connects to the server.
     * This method can be overridden to provide specific connection logic if needed.
     */
    public void connect() {
    }

    /**
     * Disconnects from the server.
     * This method can be overridden to provide specific disconnection logic if needed.
     */
    public void disconnect() {
    }

    /**
     * Retrieves the DataOutputStream for writing data to the server.
     *
     * @return the DataOutputStream object
     */
    public DataOutputStream getWriter() {
        dataOutputStream = new DataOutputStream(outputStream);
        return dataOutputStream;
    }

    /**
     * Retrieves the DataInputStream for reading data from the server.
     *
     * @return the DataInputStream object
     */
    public DataInputStream getReader() {
        dataInputStream = new DataInputStream(inputStream);
        return dataInputStream;
    }

    @Override
    public void addConsumer(StringConsumer sc) {
        consumers.add(sc);
        System.out.println("The new consumer: " + sc);
    }

    @Override
    public void removeConsumer(StringConsumer sc) {
        consumers.remove(sc);
    }

    @Override
    public void consume(String str) {
        // Handle the received message from the server
        for (StringConsumer consumer : consumers) {
            consumer.consume(str);
        }
    }
}
