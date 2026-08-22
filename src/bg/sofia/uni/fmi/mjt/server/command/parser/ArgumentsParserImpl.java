package bg.sofia.uni.fmi.mjt.server.command.parser;

import java.util.HashMap;
import java.util.Map;

public class ArgumentsParserImpl implements ArgumentsParser {

    @Override
    public Map<String, String> parse(String input) {
        Map<String, String> result = new HashMap<>();
        if (input == null || input.trim().isEmpty()) {
            return result;
        }

        String[] parts = input.split("--");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.contains("=")) {
                String[] keyValue = part.split("=", 2);
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                result.put(key, value);
            }
        }

        return result;
    }
}

