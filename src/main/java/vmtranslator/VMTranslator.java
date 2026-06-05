package vmtranslator;

import vmtranslator.parser.Parser;

import java.io.IOException;
import java.nio.file.Path;

public final class VMTranslator {
    private VMTranslator() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Uso inicial: java -jar vmtranslator.jar <arquivo.vm>");
            System.exit(1);
        }

        Parser parser = new Parser(Path.of(args[0]));
        while (parser.hasMoreCommands()) {
            System.out.println(parser.advance());
        }
    }
}
