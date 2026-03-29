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
 * Ensures that the system maintains data integrity by preventing partial updates during failures.
 */
public class EditCommandTest {
    private TradeList tradeList;
    private Storage storage;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        // Initial Trade (Index 0): AAPL, 2023-10-10, long, 150, 160, 140, Open, Trend
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
        assertEquals(ticker, current.getTicker(), "Atomicity Failure: Ticker modified");
        assertEquals(date, current.getDate(), "Atomicity Failure: Date modified");
        assertEquals(dir, current.getDirection(), "Atomicity Failure: Direction modified");
        assertEquals(entry, current.getEntryPrice(), "Atomicity Failure: Entry price modified");
        assertEquals(exit, current.getExitPrice(), "Atomicity Failure: Exit price modified");
        assertEquals(stop, current.getStopLossPrice(), "Atomicity Failure: Stop loss modified");
        assertEquals(outcome, current.getOutcome(), "Atomicity Failure: Outcome modified");
        assertEquals(strat, current.getStrategy(), "Atomicity Failure: Strategy modified");
    }

    @Test
    public void execute_validEdit_tradeUpdatedSuccessfully() throws TradeLogException {
        // Test updating specific fields (exit price and outcome)
        EditCommand command = new EditCommand("1 x/175.0 o/WIN");
        command.execute(tradeList, ui, storage);

        Trade updatedTrade = tradeList.getTrade(0);
        assertEquals(175.0, updatedTrade.getExitPrice());
        assertEquals("WIN", updatedTrade.getOutcome());
        assertEquals("AAPL", updatedTrade.getTicker());
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
        assertTradeUnchanged(0, "AAPL", "2023-10-10", "long", 150.0,
                160.0, 140.0, "Open", "Trend");

        // 2. Verify index 1 matches its new state (Outcome is "WIN")
        // Line break added to satisfy Checkstyle LineLength rule
        assertTradeUnchanged(1, "MSFT", "2024-01-01", "short", 250.0,
                230.0, 260.0, "WIN", "Swing");
    }

    @Test
    public void execute_invalidDirectionString_throwsTradeLogException() {
        EditCommand command = new EditCommand("1 dir/invalid");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
        assertEquals("long", tradeList.getTrade(0).getDirection());
    }

    @Test
    public void execute_invalidLongRisk_throwsTradeLogException() {
        // Financial logic check: stop loss cannot be above entry for long positions
        EditCommand command = new EditCommand("1 s/160.0");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
        assertEquals(140.0, tradeList.getTrade(0).getStopLossPrice());
    }

    /**
     * Verifies atomicity when a multi-field edit contains a valid ticker but an invalid stop-loss.
     * Ensures the entire transaction is rejected and no partial updates occur.
     */
    @Test
    public void execute_atomicUpdateFailure_tickerNotChanged() {
        EditCommand command = new EditCommand("1 t/TSLA s/160.0");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Deep verification of index 0 to ensure no fields were touched
        // Line break added to satisfy Checkstyle LineLength rule
        assertTradeUnchanged(0, "AAPL", "2023-10-10", "long", 150.0,
                160.0, 140.0, "Open", "Trend");
    }

    /**
     * Verifies atomicity during a complex multi-field update with an invalid outcome suffix.
     * Ensures that even if early prefixes (t/, d/, e/) are valid, the trade remains unchanged.
     */
    @Test
    public void execute_complexInvalidEdit_fullStateMaintained() {
        EditCommand command = new EditCommand("1 t/MSFT d/2025-01-01 e/500.0 o/UNKNOWN");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Line break added to satisfy Checkstyle LineLength rule
        assertTradeUnchanged(0, "AAPL", "2023-10-10", "long", 150.0,
                160.0, 140.0, "Open", "Trend");
    }

    @Test
    public void execute_indexOutOfBounds_throwsTradeLogException() {
        // Testing boundary condition: editing a non-existent trade at index 10
        EditCommand command = new EditCommand("10 t/MSFT");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
    }
}
