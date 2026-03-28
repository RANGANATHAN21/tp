package tradelog;

import java.util.Scanner;

import tradelog.exception.TradeLogException;
import tradelog.logic.command.Command;
import tradelog.logic.parser.Parser;
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

        String prompt = storage.exists()
                ? "Enter password to load trades: "
                : "No trades.txt found. Create a new password: ";

        String password = ui.readPassword(prompt);
        try {
            storage.setPassword(password);
        } catch (TradeLogException e) {
            ui.showError("Security initialization failed: " + e.getMessage());
        }

        TradeList loadedTrades;
        try {
            loadedTrades = storage.loadTrades();
            if (!loadedTrades.isEmpty()) {
                ui.showMessage("Loaded " + loadedTrades.size() + " trade(s) from storage.");
            }
        } catch (TradeLogException e) {
            ui.showError("Failed to load saved trades: " + e.getMessage());
            loadedTrades = new TradeList();
        }
        tradeList = loadedTrades;
    }

    /** Starts the main input loop. */
    public void run() {
        ui.showWelcome();
        String input;
        while ((input = ui.readCommand()) != null) {
            if (input.isEmpty()) {
                ui.showError("Command cannot be empty.");
                continue;
            }
            try {
                Command command = Parser.parseCommand(input);
                command.execute(tradeList, ui, storage);
                if (command.isExit()) {
                    break;
                }
            } catch (TradeLogException e) {
                ui.showError(e.getMessage());
            }
        }
        
        try {
            storage.saveTrades(tradeList);
            ui.showGoodbye();
            ui.showMessage("Trades saved. Goodbye!");
        } catch (TradeLogException e) {
            ui.showError(e.getMessage());
        }
        ui.closeScanner();
    }

    /**
     * Main entry point.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            java.util.logging.LogManager.getLogManager().readConfiguration(
                    TradeLog.class.getResourceAsStream("/logging.properties"));
        } catch (Exception e) {
            // fall back to default logging if config fails
        }
        new TradeLog("./data/trades.txt").run();
    }
}
