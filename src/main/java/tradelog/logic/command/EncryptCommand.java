package tradelog.logic.command;

import tradelog.model.ModeManager;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Toggles encryption on or off for the stored trade data.
 * Access is restricted in LIVE mode to prevent security configuration changes.
 */
public class EncryptCommand extends Command {

    private final String argument;

    /**
     * Constructs an EncryptCommand with the specified user argument.
     *
     * @param argument The raw string following the command word (e.g., "on", "off", "status").
     */
    public EncryptCommand(String argument) {
        assert argument != null : "Argument string should not be null";
        this.argument = argument.trim().toLowerCase();
    }

    /**
     * Executes the encryption command.
     * Restricts "on" and "off" actions in LIVE mode for security integrity.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for user feedback.
     * @param storage   The storage handler for managing encryption state.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null";
        assert ui != null : "Ui should not be null";
        assert storage != null : "Storage should not be null";

        ModeManager modeManager = ModeManager.getInstance();

        // Guardrail: Block security changes during LIVE mode
        if (modeManager.isLive() && (argument.equals("on") || argument.equals("off"))) {
            ui.showError("Security Restriction: Encryption settings cannot be modified in LIVE mode.");
            return;
        }

        // SAFE ASSERTION: Ensure no side effects for singleton access
        assert modeManager != null : "ModeManager should be initialized before executing logic";

        switch (argument) {
        case "on":
            storage.setEncryptionEnabled(true);
            ui.showMessage("Encryption enabled. Your trades will be encrypted when saved.");
            break;
        case "off":
            storage.setEncryptionEnabled(false);
            ui.showMessage("Encryption disabled. Your trades will be saved in plaintext.");
            break;
        case "status":
            String status = storage.isEncryptionEnabled() ? "enabled" : "disabled";
            ui.showMessage("Encryption is currently " + status + ".");
            break;
        default:
            ui.showError("Invalid argument. Usage: encrypt [on|off|status]");
            break;
        }
    }
}
