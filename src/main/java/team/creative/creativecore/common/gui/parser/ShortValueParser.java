package team.creative.creativecore.common.gui.parser;

@FunctionalInterface
public interface ShortValueParser {
    String parse(short v, short max);
}
