// Ron Ashkenazi 206616666
// Gal Sinai 206846743

/**
 * This interface represents a consumer that consumes strings provided by a producer.
 */
public interface StringConsumer {
    /**
     * Consumes the given string.
     *
     * @param str the string to be consumed
     */
    void consume(String str);
}