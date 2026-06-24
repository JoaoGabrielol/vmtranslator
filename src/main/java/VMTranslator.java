import codewriter.CodeWriter;
import parser.Command;
import parser.CommandType;
import parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class VMTranslator {
    private VMTranslator() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Uso: java -jar vmtranslator.jar <arquivo.vm ou diretorio>");
            System.exit(1);
        }

        Path input = Path.of(args[0]);
        if (Files.isDirectory(input)) {
            translateDirectory(input);
            return;
        }

        if (!input.getFileName().toString().endsWith(".vm")) {
            System.err.println("Entrada deve ser um arquivo .vm ou um diretorio: " + input);
            System.exit(1);
        }

        translateFile(input, asmPathForVmFile(input));
    }

    private static void translateDirectory(Path directory) throws IOException {
        List<Path> vmFiles = listVmFiles(directory);
        if (vmFiles.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo .vm encontrado em: " + directory);
        }

        String dirName = directory.getFileName().toString();
        Path output = directory.resolve(dirName + ".asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            for (Path vmFile : vmFiles) {
                translateInto(writer, vmFile);
            }
        }
    }

    private static void translateFile(Path vmFile, Path output) throws IOException {
        try (CodeWriter writer = new CodeWriter(output)) {
            translateInto(writer, vmFile);
        }
    }

    private static void translateInto(CodeWriter writer, Path vmFile) throws IOException {
        Parser parser = new Parser(vmFile);
        while (parser.hasMoreCommands()) {
            emitCommand(writer, parser.advance());
        }
    }

    private static void emitCommand(CodeWriter writer, Command command) throws IOException {
        switch (command.type()) {
            case C_ARITHMETIC -> writer.writeArithmetic(command.arg1());
            case C_PUSH -> writer.writePush(command.arg1(), command.arg2());
            case C_POP -> writer.writePop(command.arg1(), command.arg2());
            case C_LABEL -> writer.writeLabel(command.arg1());
            case C_GOTO -> writer.writeGoto(command.arg1());
            case C_IF -> writer.writeIf(command.arg1());
            case C_FUNCTION, C_CALL, C_RETURN -> throw new UnsupportedOperationException(
                    "Comando ainda nao implementado no CodeWriter: " + command.type()
            );
        }
    }

    private static List<Path> listVmFiles(Path directory) throws IOException {
        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(".vm"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
        }
    }

    private static Path asmPathForVmFile(Path input) {
        String fileName = input.getFileName().toString();
        return input.resolveSibling(fileName.substring(0, fileName.length() - 3) + ".asm");
    }
}
