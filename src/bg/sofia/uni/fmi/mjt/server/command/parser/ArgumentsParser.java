package bg.sofia.uni.fmi.mjt.server.command.parser;

import java.util.Map;

public interface ArgumentsParser {

    Map<String, String> parse(String input);
}
