package tradelog.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tradelog.exception.TradeLogException;
import tradelog.model.ModeManager;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterCommandTest {
    private TradeList tradeList;
    private Ui ui;
    private Storage storage;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        ui = new Ui();
        storage = new Storage("dummy_filter.txt");
        System.setOut(new PrintStream(outContent));
        ModeManager.getInstance().setLive(false);

        tradeList.addTrade(new Trade("AAPL", "2026-04-10", "Long", 150.0, 160.0, 145.0, "Breakout"));
        tradeList.addTrade(new Trade("TSLA", "2026-04-11", "Short", 700.0, 650.0, 720.0, "Pullback"));
    }

    @Test
    public void constructor_noCriteria_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> new FilterCommand(""));
    }

    @Test
    public void constructor_invalidStrategy_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> new FilterCommand("s/InvalidStrategyName"));
    }

    @Test
    public void execute_filterByTicker_printsExpectedTrade() throws TradeLogException {
        FilterCommand command = new FilterCommand("AAPL");
        command.execute(tradeList, ui, storage);
        assertTrue(outContent.toString().contains("AAPL"), "Output should contain the matched ticker.");
    }

    @Test
    public void execute_filterByStrategyShortcut_matchesExpandedStrategy() throws TradeLogException {
        // 假设 BB 映射到 Breakout
        FilterCommand command = new FilterCommand("s/BB");
        command.execute(tradeList, ui, storage);
        assertTrue(outContent.toString().contains("Breakout"), "Should match expanded strategy name.");
    }

    @Test
    public void execute_filterNoMatch_showsNoMatchMessage() throws TradeLogException {
        FilterCommand command = new FilterCommand("GME");
        command.execute(tradeList, ui, storage);
        assertTrue(outContent.toString().contains("No trades found matching"), "Should show no match message.");
    }

    @Test
    public void execute_filterByPartialTicker_printsExpectedTrade() throws TradeLogException {
        FilterCommand command = new FilterCommand("TS");
        command.execute(tradeList, ui, storage);
        assertTrue(outContent.toString().contains("TSLA"));
    }
}
