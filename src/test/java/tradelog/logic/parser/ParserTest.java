package tradelog.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;
import tradelog.logic.command.CompareCommand;
import tradelog.logic.command.SetModeCommand;
import tradelog.model.ModeManager;

public class ParserTest {

    @BeforeEach
    public void setUp() {
        ModeManager.getInstance().setLive(false);
    }

    @Test
    public void parseCommand_compare_returnsCompareCommand() throws TradeLogException {
        assertInstanceOf(CompareCommand.class, Parser.parseCommand("compare"));
    }

    @Test
    public void parseCommand_mode_returnsSetModeCommand() throws TradeLogException {
        assertInstanceOf(SetModeCommand.class, Parser.parseCommand("mode live"));
    }
}
