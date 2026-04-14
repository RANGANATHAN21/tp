package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.model.ModeManager;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Test class for SetModeCommand.
 * Validates mode transitions and ensures the constructor handles invalid inputs gracefully
 * according to the implementation in SetModeCommand.
 */
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
        // Reset ModeManager to a known state before each test
        ModeManager.getInstance().setMode(ModeManager.EnvironmentMode.BACKTEST);
    }

    /**
     * Helper method to simulate user input in the console.
     */
    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    public void execute_validLiveMode_updatesToLive() {
        // 1. Setup simulated input
        provideInput("yes\n");

        // 2. IMPORTANT: Re-initialize UI so its Scanner picks up the new System.in
        Ui freshUi = new Ui();

        // 3. Create and execute the command
        SetModeCommand command = new SetModeCommand("live");
        command.execute(tradeList, freshUi, storage);

        // 4. Verification
        assertTrue(ModeManager.getInstance().isLive(),
                "Mode should be LIVE after successful transition");
        assertEquals(ModeManager.EnvironmentMode.LIVE,
                ModeManager.getInstance().getCurrentMode());

        // 5. Cleanup
        System.setIn(systemIn);
    }

    @Test
    public void execute_validBacktestMode_updatesToBacktest() {
        // Step 1: Force state to LIVE first so we have something to switch FROM
        ModeManager.getInstance().setMode(ModeManager.EnvironmentMode.LIVE);
        assertTrue(ModeManager.getInstance().isLive(), "Setup: Should be in LIVE mode first");

        // Step 2: Provide "yes" for the confirmation prompt
        provideInput("yes\n");

        // IMPORTANT: Re-initialize UI after providing input to ensure the
        // Scanner inside Ui points to the current ByteArrayInputStream
        Ui testUi = new Ui();

        // Step 3: Execute the command to switch to backtest
        SetModeCommand command = new SetModeCommand("backtest");
        command.execute(tradeList, testUi, storage);

        // Step 4: Verify the state changed to false (Backtest)
        assertFalse(ModeManager.getInstance().isLive(),
                "Mode should be BACKTEST (not live) after transition");
        assertEquals(ModeManager.EnvironmentMode.BACKTEST,
                ModeManager.getInstance().getCurrentMode());

        // Step 5: Reset System.in
        System.setIn(systemIn);
    }


    /**
     * Verifies that an invalid mode string does not crash the constructor.
     * The source code catches IllegalArgumentException and sets targetMode to null.
     */
    @Test
    public void constructor_invalidMode_doesNotThrowException() {
        assertDoesNotThrow(() -> new SetModeCommand("invalid_mode"),
                "Constructor should handle invalid strings internally without throwing exceptions.");
    }

    /**
     * Verifies that empty or whitespace strings are handled gracefully by the constructor.
     */
    @Test
    public void constructor_emptyArgs_doesNotThrowException() {
        assertDoesNotThrow(() -> new SetModeCommand(""), "Should handle empty string");
        assertDoesNotThrow(() -> new SetModeCommand("   "), "Should handle whitespace string");
    }

    @Test
    public void isExit_returnsFalse() {
        SetModeCommand command = new SetModeCommand("live");
        assertFalse(command.isExit(), "SetModeCommand should not trigger application exit");
    }
}
