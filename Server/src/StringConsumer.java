// Ron Ashkenazi 206616666
// Gal Sinai 206846743

/**
 * The StringConsumer interface defines a contract for consuming strings.
 * Implementations of this interface should handle the consumption of strings.
 * It throws a ChatException in case of any errors.
 */
public interface StringConsumer {
    /**
     * Consumes the given string.
     *
     * @param str the string to be consumed
     * @throws ChatException if an error occurs while consuming the string
     */
    void consume(String str) throws ChatException;
}