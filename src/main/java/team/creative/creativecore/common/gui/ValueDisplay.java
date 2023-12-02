package team.creative.creativecore.common.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.time.TimeMath;

public interface ValueDisplay {
    Component BLOCK_TEXT = new TranslatableComponent("minecraft.block");
    Component BLOCKS_TEXT = new TranslatableComponent("minecraft.blocks");
    ValueDisplay NONE = (v, max) -> (float) Math.round(v * 100.0) / 100 + "";
    ValueDisplay PERCENT = (v, max) -> (int) ((v / max) * 100.0d) + "%";
    ValueDisplay ANGLE = (v, max) -> Math.round(v) + "°";
    ValueDisplay BLOCKS = (v, max) -> Math.round(v) + " " + (v > 1 ? BLOCKS_TEXT.getString() : BLOCK_TEXT.getString());
    ValueDisplay PIXELS = (v, max) -> Math.round(v) + "px";

    // TIME, VALUE IS THE TIME COUNTER
    ValueDisplay TIME = (v, max) -> {
        int value = (int) v;
        if (value > max) value %= (int) max;
        return TimeMath.timestamp(value) + "/" + TimeMath.timestamp((int) max);
    };
    ValueDisplay TIME_NOMAX = (v, max) -> {
        int value = (int) v;
        if (value > max) value %= (int) max;
        return TimeMath.timestamp(value);
    };

    // ASSUMES VALUE IS A TICK COUNT AND PARSE TICKS TO TIME
    ValueDisplay TIMETICK = (v, max) -> {
        int ticks = (int) v;
        if (ticks > max) ticks %= (int) max;
        return TimeMath.timestamp(Maths.tickToMs(ticks)) + "/" + TimeMath.timestamp(Maths.tickToMs((int) max));
    };
    ValueDisplay TIMETICK_NOMAX = (v, max) -> {
        int ticks = (int) v;
        if (ticks > max) ticks %= (int) max;
        return TimeMath.timestamp(Maths.tickToMs(ticks));
    };

    String get(double value, double max);
}