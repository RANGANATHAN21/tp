package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Test suite for EditCommand validation and atomic updates.
 */
public class EditCommandTest {
    private TradeList tradeList;
    private Storage storage;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        // Fixed: Use 8 arguments for Trade constructor to avoid "Expected 8 found 6"
        // Order: Ticker, Date, Direction, Entry, Exit, Stop, Outcome, Strategy
        Trade initialTrade = new Trade("AAPL", "2023-10-10", "long",
                150.0, 160.0, 140.0, "Open", "Trend");
        tradeList.addTrade(initialTrade);

        ui = new Ui();
        storage = new Storage("test_edit_storage.txt");
    }

    /**
     * Performs a deep state comparison of a trade at a specific index against expected values.
     * This is used to verify "Atomicity": ensuring that no single field has been mutated after a failed command.
     */
    private void assertTradeUnchanged(int index, String ticker, String date, String dir,
                                      double entry, double exit, double stop,
                                      String outcome, String strat) {
        Trade current = tradeList.getTrade(index);
        assertEquals(ticker, current.getTicker(), "Atomicity Failure: Ticker should not be modified");
        assertEquals(date, current.getDate(), "Atomicity Failure: Date should not be modified");
        assertEquals(dir, current.getDirection(), "Atomicity Failure: Direction should not be modified");
        assertEquals(entry, current.getEntryPrice(), "Atomicity Failure: Entry price should not be modified");
        assertEquals(exit, current.getExitPrice(), "Atomicity Failure: Exit price should not be modified");
        assertEquals(stop, current.getStopLossPrice(), "Atomicity Failure: Stop loss price should not be modified");
        assertEquals(outcome, current.getOutcome(), "Atomicity Failure: Outcome should not be modified");
        assertEquals(strat, current.getStrategy(), "Atomicity Failure: Strategy should not be modified");
    }

    @Test
    public void execute_validEdit_tradeUpdatedSuccessfully() throws TradeLogException {
        // User wants to update the exit price to 175.0 and the outcome to WIN
        EditCommand command = new EditCommand("1 x/175.0 o/WIN");

        command.execute(tradeList, ui, storage);

        Trade updatedTrade = tradeList.getTrade(0);

        // Verify that the specified fields were updated
        assertEquals(175.0, updatedTrade.getExitPrice(), "Exit price should be updated to 175.0");
        assertEquals("WIN", updatedTrade.getOutcome(), "Outcome should be updated to WIN");

        // Verify that the OTHER fields remained exactly the same
        assertEquals("AAPL", updatedTrade.getTicker(), "Ticker should remain unchanged");
        assertEquals("2023-10-10", updatedTrade.getDate(), "Date should remain unchanged");
        assertEquals(150.0, updatedTrade.getEntryPrice(), "Entry price should remain unchanged");
    }

    @Test
    public void execute_invalidDirectionString_throwsTradeLogException() {
        EditCommand command = new EditCommand("1 dir/invalid_direction");

        // Verify that the method throws TradeLogException and STOPS before calling any UI methods
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Data remains unchanged
        assertEquals("long", tradeList.getTrade(0).getDirection());
    }

    @Test
    public void execute_invalidLongRisk_throwsTradeLogException() {
        // Stop loss (160) above entry (150) for long is invalid
        EditCommand command = new EditCommand("1 s/160.0");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Stop loss price remains 140.0
        assertEquals(140.0, tradeList.getTrade(0).getStopLossPrice());
    }

    @Test
    public void execute_atomicUpdateFailure_tickerNotChanged() {
        // Attempting to change ticker to TSLA but failing at the stop loss validation step
        EditCommand command = new EditCommand("1 t/TSLA s/160.0");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Ticker must still be AAPL
        assertEquals("AAPL", tradeList.getTrade(0).getTicker());
    }

    /**
     * Simulates a complex partial-failure scenario where multiple valid prefixes are followed by an invalid one.
     * Confirms that the internal state of the Trade object remains identical to its pre-execution state.
     */
    @Test
    public void execute_complexInvalidEdit_fullStateMaintained() {
        // Attempting to change multiple fields, but failing due to an invalid outcome (UNKNOWN)
        EditCommand command = new EditCommand("1 t/MSFT d/2025-01-01 e/500.0 o/UNKNOWN");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify that even if the first few prefixes were correct, the whole trade remains in its original state
        assertTradeUnchanged(0, "AAPL", "2023-10-10", "long", 150.0, 160.0, 140.0, "Open", "Trend");
    }

    @Test
    public void execute_indexOutOfBounds_throwsTradeLogException() {
        EditCommand command = new EditCommand("10 t/MSFT");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
    }

    /**
     * Verifies that editing a second trade works correctly while index 0 remains unchanged.
     * Also used to provide diverse parameter values to eliminate IDE static analysis warnings.
     */
    @Test
    public void execute_editSecondTrade_success() throws TradeLogException {
        // Add a second trade with completely different values, including Outcome "WIN"
        Trade secondTrade = new Trade("TSLA", "2024-01-01", "short", 250.0, 230.0, 260.0, "WIN", "Swing");
        tradeList.addTrade(secondTrade);

        // Edit the second trade's ticker (Index 1 in list, "2" in user input)
        EditCommand command = new EditCommand("2 t/MSFT");
        command.execute(tradeList, ui, storage);

        // 1. Verify index 0 remains exactly as it was (Outcome is "Open")
        assertTradeUnchanged(0, "AAPL", "2023-10-10", "long", 150.0, 160.0, 140.0, "Open", "Trend");

        // 2. Verify index 1 matches its new state (Outcome is "WIN")
        // This second call with "WIN" will eliminate the 'outcome is always Open' warning
        assertTradeUnchanged(1, "MSFT", "2024-01-01", "short", 250.0, 230.0, 260.0, "WIN", "Swing");
    }
}
