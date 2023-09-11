// Ron Ashkenazi 206616666
// Gal Sinai 206846743

/**
 * Represents an exception specific to the chat functionality.
 */
public class ChatException extends Exception {

    /**
     * Constructs a new ChatException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }
}