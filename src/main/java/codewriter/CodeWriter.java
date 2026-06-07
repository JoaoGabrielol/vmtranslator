package codewriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CodeWriter implements AutoCloseable {
    private final Path output;
    private final BufferedWriter writer;
    private int labelCounter;

    public CodeWriter(Path output) throws IOException {
        this.output = output;
        this.writer = Files.newBufferedWriter(output);
    }

    public Path output() {
        return output;
    }

    public void writeArithmetic(String command) throws IOException {
        writeComment(command);
        switch (command) {
            case "add" -> write("@SP", "AM=M-1", "D=M", "A=A-1", "M=D+M");
            case "sub" -> write("@SP", "AM=M-1", "D=M", "A=A-1", "M=D-M");
            case "neg" -> write("@SP", "A=M", "A=A-1", "M=-M");
            case "eq" -> writeCompare("JEQ");
            case "gt" -> writeCompare("JGT");
            case "lt" -> writeCompare("JLT");
            case "and" -> write("@SP", "AM=M-1", "D=M", "A=A-1", "M=D&M");
            case "or" -> write("@SP", "AM=M-1", "D=M", "A=A-1", "M=D|M");
            case "not" -> write("@SP", "A=M", "A=A-1", "M=!M");
            default -> throw new UnsupportedOperationException(
                    "Comando aritmetico ainda nao implementado: " + command
            );
        }
    }

    public void writePush(String segment, int index) throws IOException {
        writeComment("push " + segment + " " + index);
        switch (segment) {
            case "constant" -> write(
                    "@" + index, "D=A", "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "local" -> write(
                    "@" + index, "D=A", "@LCL", "A=M+D", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "argument" -> write(
                    "@" + index, "D=A", "@ARG", "A=M+D", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "this" -> write(
                    "@" + index, "D=A", "@THIS", "A=M+D", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "that" -> write(
                    "@" + index, "D=A", "@THAT", "A=M+D", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "temp" -> write(
                    "@" + index, "D=A", "@5", "D=A+D", "A=D", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            default -> throw new UnsupportedOperationException(
                    "Segmento push ainda nao implementado: " + segment
            );
        }
    }

    public void writePop(String segment, int index) throws IOException {
        writeComment("pop " + segment + " " + index);
        if ("temp".equals(segment)) {
            write(
                    "@" + index, "D=A", "@5", "D=A+D", "@R13", "M=D",
                    "@SP", "AM=M-1", "D=M", "@R13", "A=M", "M=D"
            );
            return;
        }
        String base = switch (segment) {
            case "local" -> "LCL";
            case "argument" -> "ARG";
            case "this" -> "THIS";
            case "that" -> "THAT";
            default -> throw new UnsupportedOperationException(
                    "Segmento pop ainda nao implementado: " + segment
            );
        };
        write(
                "@" + index, "D=A", "@" + base, "D=M+D", "@R13", "M=D",
                "@SP", "AM=M-1", "D=M", "@R13", "A=M", "M=D"
        );
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    protected String nextLabel(String prefix) {
        return prefix + labelCounter++;
    }

    private void writeCompare(String jumpCommand) throws IOException {
        String trueLabel = nextLabel("TRUE");
        String endLabel = nextLabel("END");
        String subtraction = "JEQ".equals(jumpCommand) ? "D=D-M" : "D=M-D";

        write(
                "@SP", "AM=M-1", "D=M", "A=A-1", subtraction,
                "@" + trueLabel, "D;" + jumpCommand,
                "@SP", "A=M", "M=0",
                "@" + endLabel, "0;JMP",
                "(" + trueLabel + ")",
                "@SP", "A=M", "M=-1",
                "(" + endLabel + ")"
        );
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
