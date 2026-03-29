package tradelog.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;

/**
 * Handles reading and writing of trade data to and from a file.
 */
public class Storage {

    /** Path to the file used for persistent storage. */
    private final String filePath;

    /**
     * Constructs a Storage instance with the specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given TradeList to the storage file.
     *
     * @param tradeList The list of trades to save.
     * @throws TradeLogException If the file cannot be written.
     */
    public void saveTrades(TradeList tradeList) throws TradeLogException {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                boolean isDirCreated = file.getParentFile().mkdirs();
                if (!isDirCreated) {
                    throw new TradeLogException("Failed to create directory: "
                            + file.getParentFile().getPath());
                }
            }
            try (FileWriter writer = new FileWriter(filePath)) {
                for (int i = 0; i < tradeList.size(); i++) {
                    writer.write(tradeList.getTrade(i).toStorageString());
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            throw new TradeLogException("Failed to save trades: " + e.getMessage());
        }
    }

    /**
     * Loads trades from the storage file and returns them as a TradeList.
     *
     * @return A TradeList containing all trades loaded from the file.
     * @throws TradeLogException If the file cannot be read.
     */
    public TradeList loadTrades() throws TradeLogException {
        TradeList tradeList = new TradeList();
        File file = new File(filePath);

        if (!file.exists()) {
            return tradeList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(" \\| ");
                if (parts.length == 8) {
                    String ticker = parts[0];
                    String date = parts[1];
                    String direction = parts[2];
                    double entryPrice = Double.parseDouble(parts[3]);
                    double exitPrice = Double.parseDouble(parts[4]);
                    double stopLossPrice = Double.parseDouble(parts[5]);
                    String outcome = parts[6];
                    String strategy = parts[7];
                    tradeList.addTrade(new Trade(ticker, date, direction,
                            entryPrice, exitPrice, stopLossPrice, outcome, strategy));
                }
            }
        } catch (IOException e) {
            throw new TradeLogException("Failed to load trades");
        }

        return tradeList;
    }
}
