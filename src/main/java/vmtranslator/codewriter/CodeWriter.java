package vmtranslator.codewriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CodeWriter implements AutoCloseable {
    private final Path output;
    private final BufferedWriter writer;

    public CodeWriter(Path output) throws IOException {
        this.output = output;
        this.writer = Files.newBufferedWriter(output);
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

    @Override
    public void close() throws IOException {
        writer.close();
    }

    protected void writeComment(String command) throws IOException {
        writer.write("// " + command);
        writer.newLine();
    }

    protected void write(String... lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }
}
