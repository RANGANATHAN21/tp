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
 * Verifies that trades are only updated if all parameters are valid.
 */
public class EditCommandTest {
    private TradeList tradeList;
    private Storage storage;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        // Fixed: Providing 8 arguments to match Trade constructor:
        // Ticker, Date, Direction, Entry, Exit, Stop, Outcome, Strategy
        Trade initialTrade = new Trade("AAPL", "2023-10-10", "long",
                150.0, 160.0, 140.0, "Open", "Trend");
        tradeList.addTrade(initialTrade);

        storage = null;
        ui = null; // Passing null to confirm execution stops before UI calls on error
    }

    @Test
    public void execute_invalidDirectionString_throwsTradeLogException() {
        // Prepare: Command with an invalid direction
        EditCommand command = new EditCommand("1 dir/sideways");

        // Verify: Exception is thrown and caught by test
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Direction should still be "long"
        assertEquals("long", tradeList.getTrade(0).getDirection());
    }

    @Test
    public void execute_invalidLongRisk_throwsTradeLogException() {
        // Prepare: Setting stop loss (160) above entry (150) for a long trade
        EditCommand command = new EditCommand("1 s/160.0");

        // Verify
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Stop loss should remain 140.0
        assertEquals(140.0, tradeList.getTrade(0).getStopLossPrice());
    }

    @Test
    public void execute_invalidShortRisk_throwsTradeLogException() {
        // Prepare: Change direction to short but keep stop loss (140) below entry (150)
        EditCommand command = new EditCommand("1 dir/short s/140.0");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
    }

    @Test
    public void execute_atomicUpdateFailure_tickerNotChanged() {
        // Prepare: Attempt to change ticker to "TSLA" but trigger error with invalid stop loss
        EditCommand command = new EditCommand("1 t/TSLA s/160.0");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Ticker should still be "AAPL" because validation failed
        assertEquals("AAPL", tradeList.getTrade(0).getTicker());
    }

    @Test
    public void execute_indexOutOfBounds_throwsTradeLogException() {
        // Prepare: Edit index 5 when only 1 trade exists
        EditCommand command = new EditCommand("5 t/MSFT");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
    }
}
