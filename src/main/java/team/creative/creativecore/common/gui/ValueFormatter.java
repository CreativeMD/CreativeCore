package team.creative.creativecore.common.gui;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.math.TimeMath;

public interface ValueFormatter {
    Component BLOCK_TEXT = Component.translatable("minecraft.block");
    Component BLOCKS_TEXT = Component.translatable("minecraft.block");
    ValueFormatter NONE = (v, max) -> (Math.round(v * 100.0F) / 100F) + "";
    ValueFormatter PERCENT = (v, max) -> (int) ((v / max) * 100.0F) + "%";
    ValueFormatter ANGLE = (v, max) -> (Math.round(v * 100F) / 100F) + "°";
    ValueFormatter BLOCKS = (v, max) -> Math.round(v * 100F) / 100F + " " + (v > 1 ? BLOCKS_TEXT.getString() : BLOCK_TEXT.getString());

    ValueFormatter PIXELS = (v, max) -> Math.round(v) + "px";

    // TIME, VALUE IS THE TIME COUNTER
    ValueFormatter TIME = (v, max) -> {
        int value = (int) v;
        if (value > max) value %= (int) max;
        return TimeMath.timestamp(value) + "/" + TimeMath.timestamp((int) max);
    };
    ValueFormatter TIME_NOMAX = (v, max) -> {
        int value = (int) v;
        if (value > max) value %= (int) max;
        return TimeMath.timestamp(value);
    };

    // ASSUMES VALUE IS A TICK COUNT AND PARSE TICKS TO TIME
    ValueFormatter TIMETICK = (v, max) -> {
        int ticks = (int) v;
        if (ticks > max) ticks %= (int) max;
        return TimeMath.timestamp(Maths.tickToMs(ticks)) + "/" + TimeMath.timestamp(Maths.tickToMs((int) max));
    };
    ValueFormatter TIMETICK_NOMAX = (v, max) -> {
        int ticks = (int) v;
        if (ticks > max) ticks %= (int) max;
        return TimeMath.timestamp(Maths.tickToMs(ticks));
    };

    String get(double value, double max);
}