package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tradelog.model.ModeManager; // Added import

class ExitCommandTest {

    @BeforeEach
    public void setUp() {
        // Reset ModeManager to BACKTEST before each test for consistency
        ModeManager.getInstance().setLive(false);
    }

    @Test
    public void isExit_exitCommand_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    // Added ModeManager Assertions

    /**
     * Verifies that ExitCommand remains an exit command regardless of the mode.
     */
    @Test
    public void isExit_liveMode_returnsTrue() {
        ModeManager.getInstance().setLive(true);
        assertTrue(new ExitCommand().isExit(), "ExitCommand should still signal exit in LIVE mode.");
    }
}
