package org.example.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Small dependency-free JSON reader and writer for the web API. */
public final class JsonCodec {
    private JsonCodec() { }

    public static String stringify(Object value) {
        StringBuilder output = new StringBuilder();
        writeValue(output, value);
        return output.toString();
    }

    public static Map<String, Object> parseObject(String json) {
        Object value = new Parser(json).parse();
        if (!(value instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("A JSON object is required");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        map.forEach((key, entryValue) -> result.put((String) key, entryValue));
        return result;
    }

    private static void writeValue(StringBuilder output, Object value) {
        if (value == null) {
            output.append("null");
        } else if (value instanceof String string) {
            writeString(output, string);
        } else if (value instanceof Number || value instanceof Boolean) {
            output.append(value);
        } else if (value instanceof Map<?, ?> map) {
            output.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!(entry.getKey() instanceof String key)) {
                    throw new IllegalArgumentException("JSON object keys must be strings");
                }
                if (!first) {
                    output.append(',');
                }
                writeString(output, key);
                output.append(':');
                writeValue(output, entry.getValue());
                first = false;
            }
            output.append('}');
        } else if (value instanceof Iterable<?> values) {
            output.append('[');
            boolean first = true;
            for (Object item : values) {
                if (!first) {
                    output.append(',');
                }
                writeValue(output, item);
                first = false;
            }
            output.append(']');
        } else {
            throw new IllegalArgumentException("Unsupported JSON value: " + value.getClass().getName());
        }
    }

    private static void writeString(StringBuilder output, String value) {
        output.append('"');
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"' -> output.append("\\\"");
                case '\\' -> output.append("\\\\");
                case '\b' -> output.append("\\b");
                case '\f' -> output.append("\\f");
                case '\n' -> output.append("\\n");
                case '\r' -> output.append("\\r");
                case '\t' -> output.append("\\t");
                default -> {
                    if (character < 0x20) {
                        output.append(String.format("\\u%04x", (int) character));
                    } else {
                        output.append(character);
                    }
                }
            }
        }
        output.append('"');
    }

    private static final class Parser {
        private final String input;
        private int position;

        private Parser(String input) {
            this.input = input;
        }

        private Object parse() {
            Object value = parseValue();
            skipWhitespace();
            if (position != input.length()) {
                throw error("Unexpected trailing content");
            }
            return value;
        }

        private Object parseValue() {
            skipWhitespace();
            if (position >= input.length()) {
                throw error("Expected a JSON value");
            }
            return switch (input.charAt(position)) {
                case '{' -> parseMap();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't' -> parseLiteral("true", Boolean.TRUE);
                case 'f' -> parseLiteral("false", Boolean.FALSE);
                case 'n' -> parseLiteral("null", null);
                default -> parseNumber();
            };
        }

        private Map<String, Object> parseMap() {
            position++;
            Map<String, Object> object = new LinkedHashMap<>();
            skipWhitespace();
            if (consume('}')) {
                return object;
            }
            do {
                skipWhitespace();
                if (position >= input.length() || input.charAt(position) != '"') {
                    throw error("Expected an object key");
                }
                String key = parseString();
                skipWhitespace();
                expect(':');
                object.put(key, parseValue());
                skipWhitespace();
                if (consume('}')) {
                    return object;
                }
                expect(',');
            } while (true);
        }

        private List<Object> parseArray() {
            position++;
            List<Object> values = new ArrayList<>();
            skipWhitespace();
            if (consume(']')) {
                return values;
            }
            do {
                values.add(parseValue());
                skipWhitespace();
                if (consume(']')) {
                    return values;
                }
                expect(',');
            } while (true);
        }

        private String parseString() {
            expect('"');
            StringBuilder value = new StringBuilder();
            while (position < input.length()) {
                char character = input.charAt(position++);
                if (character == '"') {
                    return value.toString();
                }
                if (character != '\\') {
                    if (character < 0x20) {
                        throw error("Unescaped control character");
                    }
                    value.append(character);
                    continue;
                }
                if (position >= input.length()) {
                    throw error("Incomplete escape sequence");
                }
                char escaped = input.charAt(position++);
                switch (escaped) {
                    case '"', '\\', '/' -> value.append(escaped);
                    case 'b' -> value.append('\b');
                    case 'f' -> value.append('\f');
                    case 'n' -> value.append('\n');
                    case 'r' -> value.append('\r');
                    case 't' -> value.append('\t');
                    case 'u' -> value.append(parseUnicodeEscape());
                    default -> throw error("Invalid escape sequence");
                }
            }
            throw error("Unterminated string");
        }

        private char parseUnicodeEscape() {
            if (position + 4 > input.length()) {
                throw error("Incomplete unicode escape");
            }
            try {
                char result = (char) Integer.parseInt(input.substring(position, position + 4), 16);
                position += 4;
                return result;
            } catch (NumberFormatException exception) {
                throw error("Invalid unicode escape");
            }
        }

        private Object parseNumber() {
            int start = position;
            if (consume('-')) { /* sign */ }
            while (position < input.length() && Character.isDigit(input.charAt(position))) {
                position++;
            }
            if (consume('.')) {
                while (position < input.length() && Character.isDigit(input.charAt(position))) {
                    position++;
                }
            }
            if (position == start) {
                throw error("Invalid number");
            }
            String number = input.substring(start, position);
            try {
                return number.contains(".") ? Double.valueOf(number) : Long.valueOf(number);
            } catch (NumberFormatException exception) {
                throw error("Invalid number");
            }
        }

        private Object parseLiteral(String literal, Object value) {
            if (!input.startsWith(literal, position)) {
                throw error("Invalid literal");
            }
            position += literal.length();
            return value;
        }

        private void skipWhitespace() {
            while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
                position++;
            }
        }

        private boolean consume(char expected) {
            if (position < input.length() && input.charAt(position) == expected) {
                position++;
                return true;
            }
            return false;
        }

        private void expect(char expected) {
            if (!consume(expected)) {
                throw error("Expected '" + expected + "'");
            }
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException(message + " at position " + position);
        }
    }
}
