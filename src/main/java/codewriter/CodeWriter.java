package codewriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CodeWriter implements AutoCloseable {
    private final Path output;
    private final BufferedWriter writer;
    private final String fileName;
    private String currentFunction = "";
    private int labelCounter;

    public CodeWriter(Path output) throws IOException {
        this.output = output;
        this.fileName = fileNameWithoutExtension(output);
        this.writer = Files.newBufferedWriter(output);
    }

    public Path output() {
        return output;
    }

    public void writeArithmetic(String command) throws IOException {
        writeComment(command);
        switch (command) {
            case "add" -> write("@SP", "AM=M-1", "D=M", "A=A-1", "M=D+M");
            case "sub" -> write("@SP", "AM=M-1", "D=M", "A=A-1", "M=M-D");
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
                    "@LCL", "D=M", "@" + index, "A=D+A", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "argument" -> write(
                    "@ARG", "D=M", "@" + index, "A=D+A", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "this" -> write(
                    "@THIS", "D=M", "@" + index, "A=D+A", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "that" -> write(
                    "@THAT", "D=M", "@" + index, "A=D+A", "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "temp" -> write(
                    "@R" + tempAddress(index), "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "pointer" -> write(
                    "@" + pointerSymbol(index), "D=M",
                    "@SP", "A=M", "M=D", "@SP", "M=M+1"
            );
            case "static" -> write(
                    "@" + staticSymbol(index), "D=M",
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
                    "@SP", "AM=M-1", "D=M", "@R" + tempAddress(index), "M=D"
            );
            return;
        }
        if ("pointer".equals(segment)) {
            write(
                    "@SP", "AM=M-1", "D=M", "@" + pointerSymbol(index), "M=D"
            );
            return;
        }
        if ("static".equals(segment)) {
            write(
                    "@SP", "AM=M-1", "D=M", "@" + staticSymbol(index), "M=D"
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
                "@" + base, "D=M", "@" + index, "D=D+A", "@R13", "M=D",
                "@SP", "AM=M-1", "D=M", "@R13", "A=M", "M=D"
        );
    }

    public void writeLabel(String label) throws IOException {
        writeComment("label " + label);
        write("(" + flowLabel(label) + ")");
    }

    public void writeGoto(String label) throws IOException {
        writeComment("goto " + label);
        write("@" + flowLabel(label), "0;JMP");
    }

    public void writeIf(String label) throws IOException {
        writeComment("if-goto " + label);
        write(
                "@SP", "AM=M-1", "D=M",
                "@" + flowLabel(label), "D;JNE"
        );
    }

    void setCurrentFunction(String function) {
        this.currentFunction = function;
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

        write(
                "@SP", "AM=M-1", "D=M", "A=A-1", "D=M-D",
                "@" + trueLabel, "D;" + jumpCommand,
                "@SP", "A=M-1", "M=0",
                "@" + endLabel, "0;JMP",
                "(" + trueLabel + ")",
                "@SP", "A=M-1", "M=-1",
                "(" + endLabel + ")"
        );
    }

    private int tempAddress(int index) {
        if (index < 0 || index > 7) {
            throw new IllegalArgumentException("Indice do segmento temp deve estar entre 0 e 7: " + index);
        }
        return 5 + index;
    }

    private String pointerSymbol(int index) {
        return switch (index) {
            case 0 -> "THIS";
            case 1 -> "THAT";
            default -> throw new IllegalArgumentException("Indice do segmento pointer deve ser 0 ou 1: " + index);
        };
    }

    private String staticSymbol(int index) {
        return fileName + "." + index;
    }

    private String flowLabel(String label) {
        if (currentFunction.isEmpty()) {
            return label;
        }
        return currentFunction + "$" + label;
    }

    private String fileNameWithoutExtension(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
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
