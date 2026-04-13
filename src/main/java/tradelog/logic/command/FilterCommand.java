package tradelog.logic.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
    private final boolean isPartial;

    /**
     * Constructs a FilterCommand by parsing the arguments string.
     *
     * @param arguments The user-provided arguments after "filter".
     * @throws TradeLogException If no filter values are provided or validation fails.
     */
    public FilterCommand(String arguments) throws TradeLogException {
        HashMap<String, String> parsedArgs = ArgumentTokeniser.tokenise(arguments, PREFIXES);

        // Fix: Only parse ticker if it's explicitly provided and not empty to avoid ParserUtil's empty check
        String rawTicker = parsedArgs.getOrDefault("t/", "").trim();
        this.ticker = rawTicker.isEmpty() ? "" : ParserUtil.parseTicker(rawTicker);

        String rawStrategy = parsedArgs.getOrDefault("strat/", "").trim();
        this.strategy = rawStrategy.isEmpty() ? "" : ParserUtil.parseStrategy(rawStrategy);

        this.date = parsedArgs.getOrDefault("d/", "").trim();
        this.isPartial = Arrays.asList(arguments.split(" ")).contains("-p");

        // Align message with FilterCommandTest expectations
        if (this.ticker.isEmpty() && this.strategy.isEmpty() && this.date.isEmpty()) {
            throw new TradeLogException("At least one filter criteria (t/, strat/, or d/) must be provided.");
        }
    }

    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null";
        assert ui != null : "Ui should not be null";

        List<Integer> matchingIndices = new ArrayList<>();
        TradeList filteredTrades = new TradeList();

        for (int i = 0; i < tradeList.size(); i++) {
            Trade trade = tradeList.getTrade(i);
            if (isMatch(trade)) {
                matchingIndices.add(i);
                filteredTrades.addTrade(trade);
            }
        }

        if (matchingIndices.isEmpty()) {
            ui.showMessage("No trades match the filter criteria.");
        } else {
            ui.printIndexedTrades(tradeList, matchingIndices);
            SummaryCommand summaryCommand = new SummaryCommand();
            summaryCommand.execute(filteredTrades, ui, storage);
        }
    }

    private boolean isMatch(Trade trade) {
        boolean matchesTicker;
        boolean matchesStrategy;
        boolean matchesDate;

        if (isPartial) {
            matchesTicker = ticker.isEmpty() || trade.getTicker().contains(ticker.toUpperCase());
            matchesStrategy = strategy.isEmpty() ||
                    trade.getStrategy().toLowerCase().contains(strategy.toLowerCase());
            matchesDate = date.isEmpty() || trade.getDate().contains(date);
        } else {
            matchesTicker = ticker.isEmpty() || trade.getTicker().equalsIgnoreCase(ticker);
            matchesStrategy = strategy.isEmpty() || trade.getStrategy().equalsIgnoreCase(strategy);
            matchesDate = date.isEmpty() || trade.getDate().equals(date);
        }

        return matchesTicker && matchesStrategy && matchesDate;
    }
}
