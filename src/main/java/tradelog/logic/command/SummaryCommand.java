package tradelog.logic.command;

import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command to calculate and view the overall performance summary.
 * Handles both the mathematical calculations of the TradeList and the UI output.
 */
public class SummaryCommand extends Command {

    /**
     * Executes the summary command by calculating metrics directly from the TradeList
     * and displaying the formatted performance report.
     *
     * @param tradeList The current list of trades to analyze.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null when executing summary";
        assert ui != null : "Ui should not be null when executing summary";

        if (tradeList.isEmpty()) {
            ui.showSummaryEmpty();
            return;
        }

        int totalTrades = tradeList.size();
        assert totalTrades > 0 : "totalTrades must be greater than 0 for calculations";

        int winningTrades = 0;
        int losingTrades = 0;
        double totalWinR = 0;
        double totalLossR = 0;
        double totalR = 0;

        for (int i = 0; i < totalTrades; i++) {
            Trade trade = tradeList.getTrade(i);
            assert trade != null : "Trade at index " + i + " should not be null";

            double rr = trade.getRiskRewardRatio();
            totalR += rr;
            if (rr > 0) {
                winningTrades++;
                totalWinR += rr;
            } else if (rr < 0) {
                losingTrades++;
                totalLossR += Math.abs(rr);
            }
        }

        assert (winningTrades + losingTrades) <= totalTrades : "Win/Loss count exceeds total trades";

        double winRate = ((double) winningTrades / totalTrades) * 100;
        assert winRate >= 0 && winRate <= 100 : "Win rate should be between 0% and 100%";

        double averageWin = winningTrades > 0 ? (totalWinR / winningTrades) : 0;
        assert averageWin >= 0 : "Average win should be non-negative";

        double averageLoss = losingTrades > 0 ? (totalLossR / losingTrades) : 0;
        assert averageLoss >= 0 : "Average loss should be non-negative";

        double expectedValue = totalR / totalTrades;
        assert Math.abs(expectedValue - (totalR / totalTrades)) < 1e-10 :
                "EV should be totalR divided by totalTrades";
        assert Math.abs(totalR - (totalWinR - totalLossR)) < 1e-10 :
                "totalR should equal totalWinR - totalLossR";

        ui.showSummary(totalTrades, winRate, averageWin, averageLoss, expectedValue, totalR);
    }
}
