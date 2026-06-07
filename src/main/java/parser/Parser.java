package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Parser {
    private static final Set<String> ARITHMETIC_COMMANDS = Set.of(
            "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"
    );

    private static final Set<String> MEMORY_SEGMENTS = Set.of(
            "constant", "local", "argument", "this", "that", "temp", "pointer", "static"
    );

    private final List<Command> commands;
    private int current;

    public Parser(Path file) throws IOException {
        this.commands = parse(file);
    }

    public boolean hasMoreCommands() {
        return current < commands.size();
    }

    public Command advance() {
        if (!hasMoreCommands()) {
            throw new IllegalStateException("Nao ha mais comandos para ler.");
        }
        return commands.get(current++);
    }

    private static List<Command> parse(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<Command> parsed = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String clean = removeComment(removeBom(lines.get(i))).trim();
            if (clean.isEmpty()) {
                continue;
            }
            parsed.add(parseCommand(clean, i + 1));
        }

        return parsed;
    }

    private static Command parseCommand(String line, int lineNumber) {
        String[] parts = line.split("\\s+");
        String operation = parts[0];

        if (ARITHMETIC_COMMANDS.contains(operation)) {
            requireLength(parts, 1, lineNumber);
            return new Command(CommandType.C_ARITHMETIC, operation, -1, line, lineNumber);
        }

        if ("push".equals(operation) || "pop".equals(operation)) {
            requireLength(parts, 3, lineNumber);
            String segment = parts[1];
            if (!MEMORY_SEGMENTS.contains(segment)) {
                throw error("Segmento invalido '" + segment + "'", lineNumber);
            }
            int index = parseIndex(parts[2], lineNumber);
            CommandType type = "push".equals(operation) ? CommandType.C_PUSH : CommandType.C_POP;
            return new Command(type, segment, index, line, lineNumber);
        }

        throw error("Comando VM nao suportado '" + operation + "'", lineNumber);
    }

    private static String removeComment(String line) {
        int commentStart = line.indexOf("//");
        return commentStart >= 0 ? line.substring(0, commentStart) : line;
    }

    private static String removeBom(String line) {
        if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
            return line.substring(1);
        }
        return line;
    }

    private static void requireLength(String[] parts, int expected, int lineNumber) {
        if (parts.length != expected) {
            throw error("Quantidade invalida de argumentos", lineNumber);
        }
    }

    private static int parseIndex(String value, int lineNumber) {
        try {
            int index = Integer.parseInt(value);
            if (index < 0) {
                throw error("Indice nao pode ser negativo", lineNumber);
            }
            return index;
        } catch (NumberFormatException ex) {
            throw error("Indice invalido '" + value + "'", lineNumber);
        }
    }

    private static IllegalArgumentException error(String message, int lineNumber) {
        return new IllegalArgumentException("Linha " + lineNumber + ": " + message);
    }
}
