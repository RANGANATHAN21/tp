package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.model.ModeManager; // Added import
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Test suite for the {@link SummaryCommand}.
 * This class ensures 100% branch and statement coverage for the summary calculations
 * by utilizing a mock UI to intercept and verify mathematical outputs.
 */
public class SummaryCommandTest {

    /** The list of trades used as the testing environment. */
    private TradeList tradeList;

    /** A dummy storage instance to satisfy command dependencies. */
    private Storage dummyStorage;

    /** A mocked UI instance used to capture and verify calculation results. */
    private MockUi mockUi;

    /**
     * A specialized Mock UI class that intercepts the calculated metrics
     * before they are printed to the console, allowing for direct mathematical assertions.
     */
    private class MockUi extends Ui {
        /** Flag to verify if the empty summary message was triggered. */
        boolean isShowSummaryEmptyCalled = false;

        /** Captured total number of trades. */
        int capturedTotalTrades;
        /** Captured win rate percentage. */
        double capturedWinRate;
        /** Captured average win in R-multiple. */
        double capturedAverageWin;
        /** Captured average loss in R-multiple. */
        double capturedAverageLoss;
        /** Captured expected value per trade in R-multiple. */
        double capturedExpectedValue;
        /** Captured total R-multiple for all trades. */
        double capturedTotalR;

        /**
         * Intercepts the empty summary display call.
         */
        @Override
        public void showSummaryEmpty() {
            isShowSummaryEmptyCalled = true;
        }

        /**
         * Intercepts the calculated summary metrics for assertion.
         *
         * @param totalTrades   The total number of trades evaluated.
         * @param winRate       The calculated win rate percentage.
         * @param averageWin    The average positive R-multiple.
         * @param averageLoss   The average negative R-multiple (stored as a positive absolute value).
         * @param expectedValue The expected R-multiple per trade.
         * @param totalR        The net sum of all R-multiples.
         */
        @Override
        public void showSummary(int totalTrades, double winRate, double averageWin,
                                double averageLoss, double expectedValue, double totalR) {
            this.capturedTotalTrades = totalTrades;
            this.capturedWinRate = winRate;
            this.capturedAverageWin = averageWin;
            this.capturedAverageLoss = averageLoss;
            this.capturedExpectedValue = expectedValue;
            this.capturedTotalR = totalR;
        }
    }

    /**
     * Initializes the testing environment before each test method runs.
     * Sets up an empty trade list, a dummy storage object, and the mock UI.
     */
    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        dummyStorage = new Storage("dummy_summary_storage.txt");
        mockUi = new MockUi();

        // Reset ModeManager to BACKTEST before each test for consistency
        ModeManager.getInstance().setLive(false);
    }

    /**
     * Tests the execution branch where the TradeList is empty.
     * Verifies that the command correctly identifies the empty state and triggers
     * the appropriate UI warning without attempting calculations.
     */
    @Test
    public void execute_emptyList_callsShowSummaryEmpty() {
        SummaryCommand command = new SummaryCommand();

        command.execute(tradeList, mockUi, dummyStorage);

        // Verify that the empty branch was executed successfully
        assertTrue(mockUi.isShowSummaryEmptyCalled,
                "The showSummaryEmpty() method should be called when the list is empty.");
    }

    /**
     * Tests the calculation logic using a populated TradeList.
     * Injects a winning trade, a losing trade, and a break-even trade to hit
     * 100% condition coverage inside the calculation loop, verifying the mathematical accuracy.
     */
    @Test
    public void execute_populatedList_calculatesMetricsCorrectly() {
        // Trade 1: A Winning Trade (Expected RR: +2.0)
        Trade winTrade = new Trade("AAPL", "2023-10-10", "long",
                100.0, 120.0, 90.0, "Trend");

        // Trade 2: A Losing Trade (Expected RR: -0.5)
        Trade lossTrade = new Trade("TSLA", "2023-10-11", "long",
                100.0, 95.0, 90.0, "Breakout");

        // Trade 3: A Breakeven Trade (Expected RR: 0.0)
        Trade breakevenTrade = new Trade("MSFT", "2023-10-12", "long",
                100.0, 100.0, 90.0, "Trend");

        tradeList.addTrade(winTrade);
        tradeList.addTrade(lossTrade);
        tradeList.addTrade(breakevenTrade);

        SummaryCommand command = new SummaryCommand();
        command.execute(tradeList, mockUi, dummyStorage);

        // Asserting the exact math intercepted by the MockUi
        assertEquals(3, mockUi.capturedTotalTrades, "Total trades should be 3");
        assertEquals(33.333, mockUi.capturedWinRate, 0.001, "Win rate should be ~33.33%");
        assertEquals(2.0, mockUi.capturedAverageWin, 0.001, "Average win should be 2.0R");
        assertEquals(0.5, mockUi.capturedAverageLoss, 0.001, "Average loss should be 0.5R");
        assertEquals(1.5, mockUi.capturedTotalR, 0.001, "Total R should be 1.5R");
        assertEquals(0.5, mockUi.capturedExpectedValue, 0.001, "Expected Value should be 0.5R");
    }

    // Added ModeManager Assertions

    /**
     * Verifies that SummaryCommand functions correctly in LIVE mode.
     */
    @Test
    public void execute_liveMode_operatesCorrectly() {
        ModeManager.getInstance().setLive(true);
        tradeList.addTrade(new Trade("AAPL", "2026-04-14", "Long", 100, 110, 95, "Breakout"));

        SummaryCommand command = new SummaryCommand();
        command.execute(tradeList, mockUi, dummyStorage);

        assertEquals(1, mockUi.capturedTotalTrades, "SummaryCommand should work correctly in LIVE mode.");
    }
}
