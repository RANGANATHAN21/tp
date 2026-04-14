package tradelog.logic.command;

import tradelog.model.TradeList;
import tradelog.model.ModeManager;
import tradelog.model.ModeManager.EnvironmentMode;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Handles transitions between operational modes.
 * All responses are partitioned by dividers via the UI class.
 */
public class SetModeCommand extends Command {
    public static final String COMMAND_WORD = "mode";
    private final EnvironmentMode targetMode;

    public SetModeCommand(String modeStr) {
        EnvironmentMode mode;
        try {
            mode = EnvironmentMode.valueOf(modeStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            mode = null;
        }
        this.targetMode = mode;
    }

    /**
     * Executes the mode transition.
     * Order: Prompt Block -> Divider -> User Input -> Result Block.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        // Case: Invalid input (now wrapped in dividers)
        if (targetMode == null) {
            ui.showModeSimpleMessage("Error: Invalid mode. Please use 'mode live' or 'mode backtest'.");
            return;
        }

        ModeManager manager = ModeManager.getInstance();
        EnvironmentMode current = manager.getCurrentMode();

        // Case: Redundant switch (now wrapped in dividers)
        if (current == targetMode) {
            ui.showModeSimpleMessage("The application is already in " + targetMode + " mode.");
            return;
        }

        // 1. Show warning and prompt, then close the divider
        ui.showModePromptBlock(current.toString(), targetMode.toString());

        // 2. User input happens AFTER the first block is closed
        String response = ui.readCommand();

        // 3. Show the result in its own boxed block
        if (response != null && "yes".equalsIgnoreCase(response.trim())) {
            manager.setMode(targetMode);
            ui.showModeSimpleMessage("SUCCESS: Environment mode set to " + targetMode + ".");
        } else {
            ui.showModeSimpleMessage("Mode switch aborted by user.");
        }
    }

}
