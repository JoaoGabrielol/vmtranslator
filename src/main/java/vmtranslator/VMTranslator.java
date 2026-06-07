package vmtranslator;

import vmtranslator.codewriter.CodeWriter;
import vmtranslator.parser.Command;
import vmtranslator.parser.CommandType;
import vmtranslator.parser.Parser;

import java.io.IOException;
import java.nio.file.Path;

public final class VMTranslator {
    private VMTranslator() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Uso: java -jar vmtranslator.jar <arquivo.vm>");
            System.exit(1);
        }

        Path input = Path.of(args[0]);
        Path output = asmPathFor(input);
        Parser parser = new Parser(input);

        try (CodeWriter writer = new CodeWriter(output)) {
            while (parser.hasMoreCommands()) {
                Command command = parser.advance();
                switch (command.type()) {
                    case C_ARITHMETIC -> writer.writeArithmetic(command.arg1());
                    case C_PUSH -> writer.writePush(command.arg1(), command.arg2());
                    case C_POP -> writer.writePop(command.arg1(), command.arg2());
                }
            }
        }
    }

    private static Path asmPathFor(Path input) {
        String fileName = input.getFileName().toString();
        if (!fileName.endsWith(".vm")) {
            throw new IllegalArgumentException("Arquivo de entrada deve ter extensao .vm: " + input);
        }
        return input.resolveSibling(fileName.substring(0, fileName.length() - 3) + ".asm");
    }
}
