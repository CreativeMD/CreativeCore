package team.creative.creativecore.common.gui.parser;

@FunctionalInterface
public interface LongValueParser {
    String parse(long v, long max);
}
