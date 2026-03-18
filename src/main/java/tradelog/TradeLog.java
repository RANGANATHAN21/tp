package tradelog;

import java.util.Scanner;

import tradelog.exception.TradeLogException;
import tradelog.logic.command.Command;
import tradelog.logic.command.ListCommand;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Main entry point for the TradeLog application.
 */
public class TradeLog {

    private final TradeList tradeList;
    private final Ui ui;
    private final Storage storage;

    /**
     * Constructs a TradeLog instance, loading existing trades from storage.
     *
     * @param filePath Path to the file used for persistent storage.
     */
    public TradeLog(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TradeList loadedTrades;
        try {
            loadedTrades = storage.loadTrades();
        } catch (TradeLogException e) {
            ui.showError("Failed to load saved trades: " + e.getMessage());
            loadedTrades = new TradeList();
        }
        tradeList = loadedTrades;
        if (!tradeList.isEmpty()) {
            ui.showMessage("Loaded " + tradeList.size() + " trade(s) from storage.");
        }
    }

    /** Starts the main input loop. */
    public void run() {
        ui.showWelcome();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("list")) {
                Command command = new ListCommand();
                command.execute(tradeList, ui, storage);
            } else if (!input.isEmpty()) {
                ui.showError("Unknown command: " + input);
            }
        }
        scanner.close();
        try {
            storage.saveTrades(tradeList);
            ui.showMessage("Trades saved. Goodbye!");
        } catch (TradeLogException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Main entry point.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new TradeLog("./data/trades.txt").run();
    }
}
