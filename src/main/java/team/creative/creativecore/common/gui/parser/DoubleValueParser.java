package team.creative.creativecore.common.gui.parser;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.util.math.Maths;

@FunctionalInterface
public interface DoubleValueParser {
    DoubleValueParser NONE = (v, max) -> Maths.safeRound(v) + "";
    DoubleValueParser BLOCKS = (v, max) -> Component.translatable("minecraft.blocks.counting", Maths.safeRound(v)).getString();
    DoubleValueParser PERCENT = (v, max) -> (int) (Maths.safeDivide(v, max) * 100.0F) + "%";
    DoubleValueParser ANGLE = (v, max) -> Maths.safeRound(v) + "°";

    String parse(double v, double max);
}
