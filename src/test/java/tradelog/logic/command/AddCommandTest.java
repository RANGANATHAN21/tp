package tradelog.logic.command;

import org.junit.jupiter.api.Test;
import tradelog.exception.TradeLogException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the validation logic inside the AddCommand constructor.
 * Ensures that missing or blank prefixes correctly throw exceptions.
 */
public class AddCommandTest {

    /**
     * Tests if a perfectly formatted command string is accepted without throwing any exceptions.
     */
    @Test
    public void constructor_validInput_doesNotThrowException() {
        String validArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 o/win strat/Breakout";
        assertDoesNotThrow(() -> new AddCommand(validArgs));
    }

    /**
     * Tests if omitting a required prefix (e.g., missing d/) correctly throws a TradeLogException.
     */
    @Test
    public void constructor_missingPrefix_throwsTradeLogException() {
        String invalidArgs = " t/AAPL dir/long e/180 x/190 s/170 o/win strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Missing required prefix: d/"));
    }

    /**
     * Tests if including a prefix but leaving its value blank throws a TradeLogException.
     */
    @Test
    public void constructor_blankPrefixValue_throwsTradeLogException() {
        String invalidArgs = " t/ d/2026-02-18 dir/long e/180 x/190 s/170 o/win strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    /**
     * Tests if entry price equal to stop loss price throws a TradeLogException.
     */
    @Test
    public void constructor_entryEqualsStopLoss_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/180 o/win strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Entry price and stop loss price"));
    }

    /**
     * Tests if an invalid direction throws a TradeLogException.
     */
    @Test
    public void constructor_invalidDirection_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/up e/180 x/190 s/170 o/win strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Direction must be exactly"));
    }

    /**
     * Tests if a non-numeric price throws a TradeLogException.
     */
    @Test
    public void constructor_invalidPrice_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/long e/abc x/190 s/170 o/win strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("valid number"));
    }

    /**
     * Tests if a long trade with stop loss above entry price throws a TradeLogException.
     */
    @Test
    public void constructor_longTradeStopLossAboveEntry_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/190 o/win strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Long position"));
    }

    /**
     * Tests if a short trade with stop loss below entry price throws a TradeLogException.
     */
    @Test
    public void constructor_shortTradeStopLossBelowEntry_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/short e/180 x/170 s/170 o/win strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Short position"));
    }
}
