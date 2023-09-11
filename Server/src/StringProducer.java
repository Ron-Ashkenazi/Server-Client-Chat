// Ron Ashkenazi 206616666
// Gal Sinai 206846743

/**
 * The StringProducer interface defines a contract for adding and removing StringConsumers.
 * Implementations of this interface should manage the registration and removal of StringConsumers.
 */
public interface StringProducer {
    /**
     * Adds a StringConsumer to the producer's list of consumers.
     *
     * @param stringConsumer the StringConsumer to be added
     */
    void addConsumer(StringConsumer stringConsumer);

    /**
     * Removes a StringConsumer from the producer's list of consumers.
     *
     * @param stringConsumer the StringConsumer to be removed
     */
    void removeConsumer(StringConsumer stringConsumer);
}