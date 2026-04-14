package tradelog.logic.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import tradelog.model.ModeManager;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Command to display all logged trades.
 * Provides environmental context based on the current mode.
 */
public class ListCommand extends Command {

    private static final Logger logger = Logger.getLogger(ListCommand.class.getName());

    /**
     * Executes the list command by printing all trades via the UI.
     * Displays a read-only notification if the system is in LIVE mode.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler used to print the trade list.
     * @param storage   Not used by this command.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null";
        assert ui != null : "Ui should not be null";

        ModeManager modeManager = ModeManager.getInstance();

        // SAFE ASSERTION: modeManager initialized before checking state
        assert modeManager != null : "ModeManager must be accessible during list execution";

        logger.log(Level.INFO, "Executing list command. Trade count: {0}", tradeList.size());

        // Functional Modification: Inform user of read-only status in LIVE mode
        if (modeManager.isLive() && !tradeList.isEmpty()) {
            ui.showMessage("[LIVE Mode Active] Note: Historical trades are read-only.");
        }

        ui.printTradeList(tradeList);

        logger.log(Level.INFO, "List command executed successfully.");
    }
}
