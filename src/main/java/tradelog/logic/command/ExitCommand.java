package tradelog.logic.command;

import tradelog.model.ModeManager;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command to exit the TradeLog application.
 * Signals to the main application loop that execution should terminate.
 */
public class ExitCommand extends Command {

    /**
     * Executes the exit command by displaying a farewell message to the user.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null during exit";
        assert ui != null : "Ui should not be null during exit";

        // Initialize ModeManager to ensure consistency during the shutdown phase
        ModeManager modeManager = ModeManager.getInstance();
        assert modeManager != null : "ModeManager must be initialized before exit";

        ui.showGoodbye();
    }

    /**
     * Indicates that this command will terminate the application.
     *
     * @return true, as this is the exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
