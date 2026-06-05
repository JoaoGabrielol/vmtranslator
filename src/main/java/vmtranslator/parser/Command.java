package vmtranslator.parser;

public record Command(CommandType type, String arg1, int arg2, String source, int lineNumber) {
}
