package team.creative.creativecore.common.gui.parser;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.ValueParserUtil;

@FunctionalInterface
public interface DoubleValueParser {
    DoubleValueParser NONE = (v, max) -> ValueParserUtil.round(v) + "";
    DoubleValueParser BLOCKS = (v, max) -> Component.translatable("minecraft.blocks.counting", ValueParserUtil.round(v)).getString();
    DoubleValueParser PERCENT = (v, max) -> (int) (ValueParserUtil.safeDivide(v, max) * 100.0F) + "%";
    DoubleValueParser ANGLE = (v, max) -> ValueParserUtil.round(v) + "°";

    String parse(double v, double max);
}
