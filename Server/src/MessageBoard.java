// Ron Ashkenazi 206616666
// Gal Sinai 206846743

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a message board that implements the StringConsumer and StringProducer interfaces.
 */
public class MessageBoard implements StringConsumer, StringProducer {
    private List<StringConsumer> consumers;
    private List<String> userNames;

    /**
     * Constructs a new MessageBoard object.
     */
    public MessageBoard() {
        this.consumers = new ArrayList<>();
        this.userNames = new ArrayList<>();
    }

    /**
     * Adds a StringConsumer to the list of consumers.
     *
     * @param stringConsumer The StringConsumer to be added.
     */
    @Override
    public void addConsumer(StringConsumer stringConsumer) {
        consumers.add(stringConsumer);
    }

    /**
     * Removes a StringConsumer from the list of consumers.
     *
     * @param stringConsumer The StringConsumer to be removed.
     */
    @Override
    public void removeConsumer(StringConsumer stringConsumer) {
        consumers.remove(stringConsumer);
    }

    /**
     * Consumes the given string by passing it to all the StringConsumers.
     *
     * @param message The string to be consumed.
     * @throws ChatException If an error occurs while consuming the string.
     */
    @Override
    public void consume(String message) throws ChatException {
        for (StringConsumer consumer : consumers) {
            consumer.consume(message);
        }
    }

    /**
     * Broadcasts the given message to all consumers.
     *
     * @param message The message to be broadcasted.
     * @throws ChatException If an error occurs while broadcasting the message.
     */
    public void broadcast(String message) throws ChatException {
        consume(message);
    }

    /**
     * Adds a user to the list of usernames.
     *
     * @param userName The username to be added.
     */
    public void addUser(String userName) {
        userNames.add(userName);
    }

    /**
     * Removes a user from the list of usernames.
     *
     * @param userName The username to be removed.
     */
    public void removeUser(String userName) {
        userNames.remove(userName);
    }

    public int getUserNamesSize() {
        return this.userNames.size();
    }

    public String getUserNamesIndex(int i) {
        return this.userNames.get(i);
    }
}
