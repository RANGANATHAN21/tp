package tradelog.logic.command;

import java.util.HashMap;

import tradelog.exception.TradeLogException;
import tradelog.logic.parser.ArgumentTokeniser;
import tradelog.logic.parser.ParserUtil;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Command to filter trades by ticker, strategy, and/or date.
 */
public class FilterCommand extends Command {

    public static final String[] PREFIXES = {"t/", "strat/", "d/"};

    private final String ticker;
    private final String strategy;
    private final String date;

    /**
     * Constructs a FilterCommand by parsing the arguments string.
     *
     * @param arguments The user-provided arguments after "filter".
     * @throws TradeLogException If no filter values are provided.
     */
    public FilterCommand(String arguments) throws TradeLogException {
        HashMap<String, String> parsedArgs = ArgumentTokeniser.tokenise(arguments, PREFIXES);

        ticker = ParserUtil.parseTicker(parsedArgs.getOrDefault("t/", ""));
        strategy = parsedArgs.getOrDefault("strat/", "").trim();
        date = parsedArgs.getOrDefault("d/", "").trim();

        if (ticker.isEmpty() && strategy.isEmpty() && date.isEmpty()) {
            throw new TradeLogException("Use at least one filter: t/<ticker>, strat/<strategy>, d/<date>");
        }
    }

    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null";
        assert ui != null : "Ui should not be null";

        java.util.List<Integer> matchingIndices = new java.util.ArrayList<>();

        for (int i = 0; i < tradeList.size(); i++) {
            Trade trade = tradeList.getTrade(i);
            boolean matchesTicker = ticker.isEmpty() || trade.getTicker().equals(ticker);
            boolean matchesStrategy = strategy.isEmpty() || trade.getStrategy().equalsIgnoreCase(strategy);
            boolean matchesDate = date.isEmpty() || trade.getDate().equals(date);

            if (matchesTicker && matchesStrategy && matchesDate) {
                matchingIndices.add(i);
            }
        }

        if (matchingIndices.isEmpty()) {
            ui.showMessage("No trades match the filter criteria.");
        } else {
            ui.showLine();
            for (int index : matchingIndices) {
                System.out.println((index + 1) + ". " + tradeList.getTrade(index));
            }
            ui.showLine();
        }
    }
}
