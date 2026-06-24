package parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {
    @TempDir
    Path tempDir;

    @Test
    void ignoresWhitespaceAndComments() throws IOException {
        Path file = tempDir.resolve("Test.vm");
        Files.writeString(file, """
                // comentario
                push constant 7   // inline

                add
                """);

        Parser parser = new Parser(file);

        assertTrue(parser.hasMoreCommands());
        Command push = parser.advance();
        assertEquals(CommandType.C_PUSH, push.type());
        assertEquals("constant", push.arg1());
        assertEquals(7, push.arg2());

        Command add = parser.advance();
        assertEquals(CommandType.C_ARITHMETIC, add.type());
        assertEquals("add", add.arg1());
        assertFalse(parser.hasMoreCommands());
    }

    @Test
    void rejectsInvalidCommand() throws IOException {
        Path file = tempDir.resolve("Invalid.vm");
        Files.writeString(file, "dup\n");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> new Parser(file));
        assertTrue(error.getMessage().contains("Linha 1"));
    }

    @Test
    void acceptsUtf8BomAtStartOfFile() throws IOException {
        Path file = tempDir.resolve("Bom.vm");
        Files.writeString(file, "\uFEFFpush constant 3\n");

        Parser parser = new Parser(file);

        Command command = parser.advance();
        assertEquals(CommandType.C_PUSH, command.type());
        assertEquals("constant", command.arg1());
        assertEquals(3, command.arg2());
    }

    @Test
    void parsesPart2FlowAndFunctionCommands() throws IOException {
        Path file = tempDir.resolve("Part2.vm");
        Files.writeString(file, """
                label LOOP
                goto LOOP
                if-goto LOOP
                function Sys.init 0
                call Sys.main 2
                return
                """);

        Parser parser = new Parser(file);

        Command label = parser.advance();
        assertEquals(CommandType.C_LABEL, label.type());
        assertEquals("LOOP", label.arg1());
        assertEquals(-1, label.arg2());

        Command gotoCmd = parser.advance();
        assertEquals(CommandType.C_GOTO, gotoCmd.type());
        assertEquals("LOOP", gotoCmd.arg1());
        assertEquals(-1, gotoCmd.arg2());

        Command ifGoto = parser.advance();
        assertEquals(CommandType.C_IF, ifGoto.type());
        assertEquals("LOOP", ifGoto.arg1());
        assertEquals(-1, ifGoto.arg2());

        Command function = parser.advance();
        assertEquals(CommandType.C_FUNCTION, function.type());
        assertEquals("Sys.init", function.arg1());
        assertEquals(0, function.arg2());

        Command call = parser.advance();
        assertEquals(CommandType.C_CALL, call.type());
        assertEquals("Sys.main", call.arg1());
        assertEquals(2, call.arg2());

        Command returnCmd = parser.advance();
        assertEquals(CommandType.C_RETURN, returnCmd.type());
        assertEquals("", returnCmd.arg1());
        assertEquals(-1, returnCmd.arg2());

        assertFalse(parser.hasMoreCommands());
    }
}
