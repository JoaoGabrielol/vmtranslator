import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VMTranslatorTest {
    @TempDir
    Path tempDir;

    @Test
    void translatesDirectoryWithoutSysVmWithoutBootstrap() throws IOException {
        Path input = Files.createDirectory(tempDir.resolve("Flow"));
        Files.writeString(input.resolve("Flow.vm"), """
                label LOOP
                push constant 1
                if-goto LOOP
                """);

        VMTranslator.main(new String[]{input.toString()});

        String asm = Files.readString(input.resolve("Flow.asm"));
        assertTrue(asm.contains("(LOOP)"));
        assertTrue(asm.contains("@LOOP"));
        assertFalse(asm.contains("@Sys.init"));
    }

    @Test
    void translatesDirectoryWithSysVmUsingBootstrap() throws IOException {
        Path input = Files.createDirectory(tempDir.resolve("NestedCall"));
        Files.writeString(input.resolve("Sys.vm"), """
                function Sys.init 0
                call Main.main 0
                label LOOP
                goto LOOP
                """);
        Files.writeString(input.resolve("Main.vm"), """
                function Main.main 1
                push constant 7
                return
                """);

        VMTranslator.main(new String[]{input.toString()});

        String asm = Files.readString(input.resolve("NestedCall.asm"));
        assertTrue(asm.contains("@256"));
        assertTrue(asm.contains("@Sys.init"));
        assertTrue(asm.contains("(Main.main)"));
        assertTrue(asm.contains("@Main.main"));
        assertTrue(asm.contains("@R13"));
        assertTrue(asm.contains("@R14"));
    }

    @Test
    void translatesStaticsWithPerFileSymbolsInDirectory() throws IOException {
        Path input = Files.createDirectory(tempDir.resolve("Statics"));
        Files.writeString(input.resolve("Alpha.vm"), """
                push constant 3
                pop static 0
                """);
        Files.writeString(input.resolve("Beta.vm"), """
                push static 0
                pop static 1
                """);

        VMTranslator.main(new String[]{input.toString()});

        String asm = Files.readString(input.resolve("Statics.asm"));
        assertTrue(asm.contains("@Alpha.0"));
        assertTrue(asm.contains("@Beta.0"));
        assertTrue(asm.contains("@Beta.1"));
        assertFalse(asm.contains("@Statics.0"));
    }
}
