package tradelog.logic.command;

import tradelog.exception.TradeLogException;
import tradelog.logic.parser.ParserUtil;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.util.ArrayList;
import java.util.List;

public class FilterCommand extends Command {
    private final String criteria;

    public FilterCommand(String args) throws TradeLogException {
        if (args == null || args.trim().isEmpty()) {
            throw new TradeLogException("Filter criteria cannot be empty! Use 'filter TICKER' or 'filter s/STRATEGY'.");
        }
        this.criteria = args.trim();

        // verify strategy if there is any
        if (criteria.startsWith("s/")) {
            String strategy = criteria.substring(2).trim();
            if (strategy.isEmpty() || !ParserUtil.getStrategyShortcuts().containsValue(strategy)
                    && !ParserUtil.getStrategyShortcuts().containsKey(strategy)) {
                throw new TradeLogException("Invalid strategy: " + strategy);
            }
        }
    }

    @Override
    public void execute(TradeList trades, Ui ui, Storage storage) {
        List<Integer> matchedIndices = new ArrayList<>();
        String searchKey = criteria.startsWith("s/") ? criteria.substring(2).trim() : criteria;
        boolean isStrategySearch = criteria.startsWith("s/");

        // e.g. BB -> Breakout
        String finalSearchKey = isStrategySearch ?
                ParserUtil.getStrategyShortcuts().getOrDefault(searchKey.toUpperCase(), searchKey) : searchKey;

        for (int i = 0; i < trades.size(); i++) {
            Trade t = trades.getTrade(i);
            if (isStrategySearch) {
                if (t.getStrategy().equalsIgnoreCase(finalSearchKey)) {
                    matchedIndices.add(i);
                }
            } else {
                if (t.getTicker().toUpperCase().contains(finalSearchKey.toUpperCase())) {
                    matchedIndices.add(i);
                }
            }
        }

        if (matchedIndices.isEmpty()) {
            ui.showMessage("No trades found matching: " + criteria);
        } else {
            ui.printIndexedTrades(trades, matchedIndices);
        }
    }
}
