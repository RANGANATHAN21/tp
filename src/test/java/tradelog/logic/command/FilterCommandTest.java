package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Test suite for FilterCommand, ensuring criteria validation and aggregate calculations.
 */
public class FilterCommandTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(buffer));
        action.run();
        System.setOut(original);
        return buffer.toString();
    }

    @Test
    public void constructor_noCriteria_throwsTradeLogException() {
        String args = "";
        TradeLogException ex = assertThrows(TradeLogException.class, () -> new FilterCommand(args));
        // Match the message in your FilterCommand implementation
        assertTrue(ex.getMessage().contains("At least one filter criteria"));
    }

    @Test
    public void constructor_withValidCriteria_doesNotThrow() {
        String args = "t/AAPL";
        assertDoesNotThrow(() -> new FilterCommand(args));
    }

    @Test
    public void constructor_invalidStrategy_throwsTradeLogException() {
        TradeLogException ex = assertThrows(TradeLogException.class, () ->
                new FilterCommand("strat/INVALID"));
        // Ensure this matches ParserUtil's strategy error message
        assertTrue(ex.getMessage().contains("Invalid strategy"));
    }

    @Test
    public void execute_filterByTicker_printsExpectedTrade() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 110, 95, "Breakout"));
        tradeList.addTrade(new Trade("MSFT", "2026-03-02", "Short",
                200, 190, 210, "Momentum"));

        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");

        FilterCommand command = new FilterCommand("t/AAPL");
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));

        assertTrue(output.contains("AAPL | 2026-03-01 | Long"));
        assertFalse(output.contains("MSFT | 2026-03-02 | Short"));
        assertTrue(output.contains("Total Trades: 1"));
    }

    @Test
    public void execute_filterByStrategy_calculatesCorrectAggregates() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 110, 95, "Breakout"));
        tradeList.addTrade(new Trade("TSLA", "2026-03-03", "Long",
                100, 90, 95, "Breakout"));

        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");

        FilterCommand command = new FilterCommand("strat/Breakout");
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));

        assertTrue(output.contains("Total Trades: 2"));
        assertTrue(output.contains("Win Rate: 50%"));
    }

    @Test
    public void execute_filterByStrategyShortcut_matchesExpandedStrategy() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 110, 95, "Breakout"));
        tradeList.addTrade(new Trade("TSLA", "2026-03-03", "Long",
                100, 90, 95, "Pullback"));

        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");

        FilterCommand command = new FilterCommand("strat/BB");
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));

        assertTrue(output.contains("AAPL | 2026-03-01 | Long"));
        assertFalse(output.contains("TSLA | 2026-03-03 | Long"));
    }

    @Test
    public void execute_filterNoMatch_showsNoMatchMessage() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 110, 95, "Breakout"));

        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");

        FilterCommand command = new FilterCommand("t/GOOG");
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));

        assertTrue(output.contains("No trades match the filter criteria."));
    }

    @Test
    public void execute_filterByPartialTicker_printsExpectedTrade() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 110, 95, "Breakout"));
        tradeList.addTrade(new Trade("MSFT", "2026-03-02", "Short",
                200, 190, 210, "Momentum"));

        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");

        FilterCommand command = new FilterCommand("-p t/AP");
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));

        assertTrue(output.contains("AAPL | 2026-03-01 | Long"));
        assertFalse(output.contains("MSFT | 2026-03-02 | Short"));
    }
}
