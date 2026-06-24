package codewriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeWriterTest {
    @TempDir
    Path tempDir;

    @Test
    void writesSubWithCorrectOperandOrder() throws IOException {
        Path output = tempDir.resolve("Sub.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.writeArithmetic("sub");
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("M=M-D"));
        assertFalse(asm.contains("M=D-M"));
    }

    @Test
    void writesPointerAndStaticSegments() throws IOException {
        Path output = tempDir.resolve("StaticTest.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.writePush("pointer", 0);
            writer.writePop("pointer", 1);
            writer.writePush("static", 3);
            writer.writePop("static", 7);
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("@THIS"));
        assertTrue(asm.contains("@THAT"));
        assertTrue(asm.contains("@StaticTest.3"));
        assertTrue(asm.contains("@StaticTest.7"));
    }

    @Test
    void writesValidAddressCalculations() throws IOException {
        Path output = tempDir.resolve("Address.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.writePush("local", 2);
            writer.writePop("argument", 3);
            writer.writePush("temp", 4);
            writer.writePop("temp", 5);
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("A=D+A"));
        assertTrue(asm.contains("D=D+A"));
        assertTrue(asm.contains("@R9"));
        assertTrue(asm.contains("@R10"));
        assertFalse(asm.contains("A=M+D"));
        assertFalse(asm.contains("D=M+D"));
        assertFalse(asm.contains("D=A+D"));
    }

    @Test
    void writesFlowControlWithoutFunctionPrefix() throws IOException {
        Path output = tempDir.resolve("Flow.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.writeLabel("LOOP_START");
            writer.writeGoto("LOOP_START");
            writer.writeIf("LOOP_START");
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("(LOOP_START)"));
        assertTrue(asm.contains("@LOOP_START"));
        assertTrue(asm.contains("0;JMP"));
        assertTrue(asm.contains("D;JNE"));
        assertTrue(asm.contains("@SP"));
        assertTrue(asm.contains("AM=M-1"));
    }

    @Test
    void writesFlowControlWithFunctionPrefix() throws IOException {
        Path output = tempDir.resolve("FlowFunction.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.setCurrentFunction("Sys.main");
            writer.writeLabel("LOOP");
            writer.writeGoto("LOOP");
            writer.writeIf("LOOP");
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("(Sys.main$LOOP)"));
        assertTrue(asm.contains("@Sys.main$LOOP"));
        assertFalse(asm.contains("(LOOP)"));
    }

    @Test
    void writesStaticSymbolsWithConfiguredFileName() throws IOException {
        Path output = tempDir.resolve("Directory.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.setFileName("Sys.vm");
            writer.writePush("static", 2);
            writer.setFileName("Main");
            writer.writePop("static", 4);
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("@Sys.2"));
        assertTrue(asm.contains("@Main.4"));
        assertFalse(asm.contains("@Directory.2"));
    }

    @Test
    void writesFunctionLabelAndInitializesLocals() throws IOException {
        Path output = tempDir.resolve("Function.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.writeFunction("SimpleFunction.test", 2);
            writer.writeLabel("LOOP");
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("(SimpleFunction.test)"));
        assertTrue(asm.contains("(SimpleFunction.test$LOOP)"));
        assertTrue(occurrences(asm, "M=0") >= 2);
    }

    @Test
    void writesCallFrameWithUniqueReturnLabel() throws IOException {
        Path output = tempDir.resolve("Call.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.writeCall("Sys.main", 0);
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("@Sys.main$ret.0"));
        assertTrue(asm.contains("(Sys.main$ret.0)"));
        assertTrue(asm.contains("@LCL"));
        assertTrue(asm.contains("@ARG"));
        assertTrue(asm.contains("@THIS"));
        assertTrue(asm.contains("@THAT"));
        assertTrue(asm.contains("@5"));
        assertTrue(asm.contains("@0"));
        assertTrue(asm.contains("@Sys.main"));
        assertTrue(asm.contains("0;JMP"));
    }

    @Test
    void writesReturnRestoringCallerFrame() throws IOException {
        Path output = tempDir.resolve("Return.asm");

        try (CodeWriter writer = new CodeWriter(output)) {
            writer.writeReturn();
        }

        String asm = Files.readString(output);
        assertTrue(asm.contains("@R13"));
        assertTrue(asm.contains("@R14"));
        assertTrue(asm.contains("@THAT"));
        assertTrue(asm.contains("@THIS"));
        assertTrue(asm.contains("@ARG"));
        assertTrue(asm.contains("@LCL"));
        assertTrue(asm.contains("A=M"));
        assertTrue(asm.contains("0;JMP"));
    }

    private static int occurrences(String text, String token) {
        int count = 0;
        int index = text.indexOf(token);
        while (index >= 0) {
            count++;
            index = text.indexOf(token, index + token.length());
        }
        return count;
    }
}
