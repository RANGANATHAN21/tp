package tradelog.logic.parser;

import tradelog.exception.TradeLogException;

/**
 * Utility class containing methods for parsing and validating specific data types.
 * Helps keep the main Parser class clean and focused on routing commands.
 */
public class ParserUtil {

    /**
     * Parses a string representation of a price into a double.
     *
     * @param priceString The string representing the price.
     * @param fieldName   The name of the field (e.g., "Entry", "Exit") for error messages.
     * @return The parsed double value.
     * @throws TradeLogException If the string cannot be converted to a valid number.
     */
    public static double parsePrice(String priceString, String fieldName) throws TradeLogException {
        try {
            return Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            throw new TradeLogException("The " + fieldName + " price must be a valid number!");
        }
    }

    /**
     * Parses and formats a ticker symbol to be consistently uppercase.
     *
     * @param ticker The raw ticker string.
     * @return The formatted uppercase ticker.
     */
    public static String parseTicker(String ticker) {
        return ticker.trim().toUpperCase();
    }

    /**
     * Parses and validates the trade direction.
     *
     * @param direction The raw direction string.
     * @return The formatted direction ("Long" or "Short").
     * @throws TradeLogException If the direction is not "long" or "short".
     */
    public static String parseDirection(String direction) throws TradeLogException {
        String dir = direction.trim().toLowerCase();
        if (dir.equals("long") || dir.equals("short")) {
            return dir.substring(0, 1).toUpperCase() + dir.substring(1);
        }
        throw new TradeLogException("Direction must be exactly 'long' or 'short'!");
    }

    /**
     * Validates that the entry price and stop loss price are not equal.
     *
     * @param entryPrice    The entry price of the trade.
     * @param stopLossPrice The stop loss price of the trade.
     * @throws TradeLogException If entry price equals stop loss price.
     */
    public static void validatePrices(double entryPrice, double stopLossPrice)
            throws TradeLogException {
        if (entryPrice == stopLossPrice) {
            throw new TradeLogException("Entry price and stop loss price cannot have the same value.");
        }
    }

    /**
     * Validates that the stop loss price is on the correct side of the entry price.
     *
     * @param direction     The trade direction ("long" or "short").
     * @param entryPrice    The entry price of the trade.
     * @param stopLossPrice The stop loss price of the trade.
     * @throws TradeLogException If the stop loss is on the wrong side of the entry price.
     */
    public static void validateStopLoss(String direction, double entryPrice, double stopLossPrice)
            throws TradeLogException {
        if (direction.equals("long") && stopLossPrice > entryPrice) {
            throw new TradeLogException(
                    "Invalid Trade: For a Long position, Stop Loss must be below Entry Price.");
        }
        if (direction.equals("short") && stopLossPrice < entryPrice) {
            throw new TradeLogException(
                    "Invalid Trade: For a Short position, Stop Loss must be above Entry Price.");
        }
    }
}
