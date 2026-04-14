package tradelog.logic.command;

import org.junit.jupiter.api.BeforeEach; // Added import
import org.junit.jupiter.api.Test;

import tradelog.model.ModeManager; // Added import
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for UndoCommand.
 */
public class UndoCommandTest {

    @BeforeEach
    public void setUp() {
        // Reset ModeManager to BACKTEST before each test for consistency
        ModeManager.getInstance().setLive(false);
    }

    /**
     * Tests undo after adding a trade.
     */
    @Test
    public void execute_addThenUndo_success() {
        TradeList tradeList = new TradeList();
        Ui ui = new Ui();
        Storage storage = null; // not needed for this test

        Trade trade = new Trade("AAPL", "2026-03-31", "long",
                180, 190, 170, "Breakout");

        // Save initial state (empty)
        UndoCommand.saveState(tradeList);

        tradeList.addTrade(trade);
        assertEquals(1, tradeList.size());

        // Undo
        UndoCommand undoCommand = new UndoCommand();
        undoCommand.execute(tradeList, ui, storage);

        assertEquals(0, tradeList.size());
    }

    /**
     * Tests undo after deleting a trade.
     */
    @Test
    public void execute_deleteThenUndo_success() {
        TradeList tradeList = new TradeList();
        Ui ui = new Ui();
        Storage storage = null;

        Trade trade = new Trade("TSLA", "2026-03-31", "long",
                250, 260, 240, "Pullback");

        tradeList.addTrade(trade);

        // Save state before deletion
        UndoCommand.saveState(tradeList);

        tradeList.deleteTrade(0);
        assertEquals(0, tradeList.size());

        // Undo
        new UndoCommand().execute(tradeList, ui, storage);

        assertEquals(1, tradeList.size());
        assertEquals("TSLA", tradeList.getTrade(0).getTicker());
    }

    /**
     * Tests undo when no previous state exists.
     */
    @Test
    public void execute_noPreviousState_noChange() {
        TradeList tradeList = new TradeList();
        Ui ui = new Ui();
        Storage storage = null;

        new UndoCommand().execute(tradeList, ui, storage);

        assertEquals(0, tradeList.size());
    }

    /**
     * Tests that undo only works once (one-step limit).
     */
    @Test
    public void execute_multipleUndo_onlyOneStep() {
        TradeList tradeList = new TradeList();
        Ui ui = new Ui();
        Storage storage = null;

        Trade trade = new Trade("NVDA", "2026-03-31", "long",
                900, 920, 880, "Breakout");

        UndoCommand.saveState(tradeList);
        tradeList.addTrade(trade);

        UndoCommand undo = new UndoCommand();
        undo.execute(tradeList, ui, storage);

        assertEquals(0, tradeList.size());

        // Second undo should do nothing
        undo.execute(tradeList, ui, storage);

        assertEquals(0, tradeList.size());
    }

    // Added ModeManager Assertions

    /**
     * Verifies that UndoCommand operates correctly even in LIVE mode.
     */
    @Test
    public void execute_liveMode_operatesCorrectly() {
        ModeManager.getInstance().setLive(true);
        TradeList tradeList = new TradeList();
        Ui ui = new Ui();

        UndoCommand.saveState(tradeList);
        tradeList.addTrade(new Trade("AAPL", "2026-04-14", "Long", 100, 110, 95, "Breakout"));

        new UndoCommand().execute(tradeList, ui, null);

        assertEquals(0, tradeList.size(), "UndoCommand should function in LIVE mode.");
    }
}
