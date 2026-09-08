package bg.sofia.uni.fmi.mjt.command.serverCommand.parser;

import java.util.Map;

public interface ArgumentsParser {

    Map<String, String> parse(String input);
}
