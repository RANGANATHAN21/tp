package tradelog.exception;

/**
 * Represents exceptions specific to the TradeLog application.
 */
public class TradeLogException extends RuntimeException {

    /**
     * Constructs a TradeLogException with the specified message.
     *
     * @param message The error message.
     */
    public TradeLogException(String message) {
        super(message);
    }
}
