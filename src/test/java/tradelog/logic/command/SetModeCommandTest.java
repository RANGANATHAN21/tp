package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;
import tradelog.model.ModeManager;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class SetModeCommandTest {

    private TradeList tradeList;
    private Ui ui;
    private Storage storage;
    private final InputStream systemIn = System.in;

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        ui = new Ui();
        storage = new Storage("dummy_set_mode_storage.txt");
        ModeManager.getInstance().setLive(false);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    public void execute_validLiveMode_updatesToLive() throws TradeLogException {
        provideInput("yes\n"); // 模拟确认
        SetModeCommand command = new SetModeCommand("live");
        command.execute(tradeList, new Ui(), storage);

        assertTrue(ModeManager.getInstance().isLive());
        assertEquals(ModeManager.EnvironmentMode.LIVE, ModeManager.getInstance().getCurrentMode());
        System.setIn(systemIn);
    }

    @Test
    public void execute_validBacktestMode_updatesToBacktest() throws TradeLogException {
        ModeManager.getInstance().setLive(true);
        provideInput("yes\n");
        SetModeCommand command = new SetModeCommand("backtest");
        command.execute(tradeList, new Ui(), storage);

        assertFalse(ModeManager.getInstance().isLive());
        System.setIn(systemIn);
    }

    @Test
    public void constructor_invalidMode_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> new SetModeCommand("invalid_mode"));
    }

    @Test
    public void constructor_emptyArgs_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> new SetModeCommand(""));
        assertThrows(TradeLogException.class, () -> new SetModeCommand("   "));
    }

    @Test
    public void isExit_returnsFalse() throws TradeLogException {
        assertFalse(new SetModeCommand("live").isExit());
    }
}
