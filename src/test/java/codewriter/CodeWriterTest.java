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
}
