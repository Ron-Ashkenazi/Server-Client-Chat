// Ron Ashkenazi 206616666
// Gal Sinai 206846743

/**
 * This interface represents a producer that provides strings to consumers.
 */
public interface StringProducer {
    /**
     * Adds a consumer to the list of consumers.
     *
     * @param stringConsumer the StringConsumer to be added
     */
    void addConsumer(StringConsumer stringConsumer);

    /**
     * Removes a consumer from the list of consumers.
     *
     * @param stringConsumer the StringConsumer to be removed
     */
    void removeConsumer(StringConsumer stringConsumer);
}