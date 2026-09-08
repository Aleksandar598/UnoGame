package bg.sofia.uni.fmi.mjt.server.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.HelpCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpCommandTest {
    @Test
    void testListsPublicAndInGameCommands() throws Exception {
        String help = new HelpCommand().execute("help", null);

        assertTrue(help.startsWith("Available commands:"));
        assertTrue(help.contains("login --username=<username> --password=<password>"));
        assertTrue(help.contains("create-game --game-id=<id>"));
        assertTrue(help.contains("play --card-id=<id>"));
    }
}
