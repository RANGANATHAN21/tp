package tradelog.model;

import java.time.LocalDate;

/**
 * Singleton manager responsible for maintaining the global execution state of the application.
 * * The system operates in two distinct modes:
 * 1. BACKTEST: A sandbox environment for historical analysis without validation constraints.
 * 2. LIVE: A disciplined environment that enforces real-time trading rules, automatic date
 * stamping, and risk management (Daily Loss Limit) protocols.
 * * This class resides in the Model layer as it represents a core piece of application metadata
 * that influences the behavior of various logic commands.
 */
public class ModeManager {
    public enum EnvironmentMode {
        LIVE, BACKTEST
    }

    private static ModeManager instance;
    private EnvironmentMode currentMode = EnvironmentMode.BACKTEST;

    private ModeManager() {}

    /**
     * Returns the singleton instance of ModeManager.
     * @return The active ModeManager instance.
     */
    public static ModeManager getInstance() {
        if (instance == null) {
            instance = new ModeManager();
        }
        return instance;
    }

    public void setMode(EnvironmentMode mode) {
        this.currentMode = mode;
    }

    /**
     * Sets the mode based on a boolean value to support legacy calls and testing.
     * @param isLive true for LIVE mode, false for BACKTEST mode.
     */
    public void setLive(boolean isLive) {
        this.currentMode = isLive ? EnvironmentMode.LIVE : EnvironmentMode.BACKTEST;
    }

    public EnvironmentMode getCurrentMode() {
        return currentMode;
    }

    public boolean isLive() {
        return currentMode == EnvironmentMode.LIVE;
    }

    /**
     * Returns a detailed warning message outlining the restrictions of LIVE mode.
     * @return String containing current date and live mode constraints.
     */
    public String getWarningMessage() {
        return "WARNING: Live mode enforces strict discipline:\n"
                + "- Only trades with today's date (" + LocalDate.now() + ") can be added.\n"
                + "- Daily Loss Limit checks (setloss) will be active.\n"
                + "- Edits to historical trade dates will be restricted.";
    }
}
