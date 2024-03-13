package team.creative.creativecore.common.gui;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.util.math.TimeMath;

@FunctionalInterface
public interface ValueParser {
    Component BLOCK_TEXT = Component.translatable("minecraft.block");
    Component BLOCKS_TEXT = Component.translatable("minecraft.block");

    ValueParser NONE = (v, max) -> (Math.round(v * 100.0F) / 100F) + "";
    ValueParser PERCENT = (v, max) -> (int) ((v / max) * 100.0F) + "%";
    ValueParser ANGLE = (v, max) -> (Math.round(v * 100F) / 100F) + "°";
    ValueParser BLOCKS = (v, max) -> Math.round(v * 100F) / 100F + " " + (v > 1 ? BLOCKS_TEXT.getString() : BLOCK_TEXT.getString());

    ValueParser PIXELS = (v, max) -> Math.round(v) + "px";

    // TIME, VALUE IS THE TIME COUNTER
    ValueParser TIME = (v, max) -> {
        int value = (int) v;
        if (value > max) value %= (int) max;
        return TimeMath.timestamp(value) + "/" + TimeMath.timestamp((int) max);
    };
    ValueParser TIME_NOMAX = (v, max) -> {
        int value = (int) v;
        if (value > max) value %= (int) max;
        return TimeMath.timestamp(value);
    };

    String parse(double value, double max);
}