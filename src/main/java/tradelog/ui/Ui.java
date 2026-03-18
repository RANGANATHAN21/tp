package tradelog.ui;

import tradelog.model.Trade;
import tradelog.model.TradeList;

/**
 * Handles all user interaction output for TradeLog.
 */
public class Ui {

    private static final String DIVIDER = "-".repeat(80);

    /** Prints the welcome banner shown on startup. */
    public void showWelcome() {
        showLine();
        System.out.println("Welcome to TradeLog!");
        System.out.println("Commands: add, list, edit, delete, summary, exit");
        showLine();
    }

    /** Prints the goodbye message shown when exiting the app. */
    public void showGoodbye() {
        showLine();
        System.out.println("Goodbye! May your profits be high and your losses small.");
        showLine();
    }

    /** Prints a horizontal divider line. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints a general message to the user.
     *
     * @param message The message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prints all trades in the given TradeList, numbered from 1.
     * Prints an empty-list message if there are no trades.
     *
     * @param tradeList The list of trades to display.
     */
    public void printTradeList(TradeList tradeList) {
        showLine();
        if (tradeList.isEmpty()) {
            System.out.println("No trades logged yet.");
        } else {
            for (int i = 0; i < tradeList.size(); i++) {
                System.out.println((i + 1) + ". " + tradeList.getTrade(i).toStorageString());
            }
        }
        showLine();
    }

    /**
     * Prints the summary of a single trade.
     *
     * @param trade The trade to display.
     */
    public void printTrade(Trade trade) {
        showLine();
        System.out.println(trade.toSummaryString());
        showLine();
    }

    /** Prints a message confirming a trade was added. */
    public void showTradeAdded() {
        System.out.println("Trade successfully added.");
    }

    /** Prints a message confirming a trade was deleted. */
    public void showTradeDeleted() {
        System.out.println("Trade successfully deleted.");
    }

    /**
     * Prints a message confirming a trade was updated.
     *
     * @param index The 1-based index of the updated trade.
     */
    public void showTradeUpdated(int index) {
        System.out.println("Trade " + index + " updated successfully.");
    }

    /**
     * Prints an error message to the user.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        showLine();
        System.out.println("Error: " + message);
        showLine();
    }
}
