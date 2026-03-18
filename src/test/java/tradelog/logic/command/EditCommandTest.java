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

        storage = null;
        ui = null; // UI is null because execute should throw exception before using it
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

    @Test
    public void execute_indexOutOfBounds_throwsTradeLogException() {
        EditCommand command = new EditCommand("10 t/MSFT");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
    }
}
