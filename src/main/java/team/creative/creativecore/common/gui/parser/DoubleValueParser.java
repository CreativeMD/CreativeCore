package team.creative.creativecore.common.gui.parser;

@FunctionalInterface
public interface DoubleValueParser {
    String parse(double v, double max);
}
