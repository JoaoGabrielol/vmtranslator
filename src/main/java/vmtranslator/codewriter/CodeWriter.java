package vmtranslator.codewriter;

import java.nio.file.Path;

public final class CodeWriter {
    private final Path output;

    public CodeWriter(Path output) {
        this.output = output;
    }

    public Path output() {
        return output;
    }

    public void writeArithmetic(String command) {
        throw new UnsupportedOperationException("TODO: implementar operacoes aritmeticas/logicas");
    }

    public void writePush(String segment, int index) {
        throw new UnsupportedOperationException("TODO: implementar comando push");
    }

    public void writePop(String segment, int index) {
        throw new UnsupportedOperationException("TODO: implementar comando pop");
    }
}
