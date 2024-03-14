package team.creative.creativecore.common.gui;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.parser.DoubleValueParser;
import team.creative.creativecore.common.gui.parser.IntValueParser;
import team.creative.creativecore.common.gui.parser.LongValueParser;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.math.TimeMath;

public class ValueParsers {
    public static final DoubleValueParser BLOCKS = (v, max) -> Component.translatable("minecraft.blocks.counting", round(v)).getString();
    public static final DoubleValueParser NONE = (v, max) -> String.valueOf(round(v));
    public static final DoubleValueParser PERCENT = (v, max) -> (int) (safeDivide(v, max) * 100.0F) + "%";
    public static final DoubleValueParser ANGLE = (v, max) -> round(v) + "°";
    public static final IntValueParser PIXELS = (v, max) -> v + "px";
    public static final LongValueParser TIME = (v, max) -> TimeMath.timestamp((v > max) ? safePercent(v, max) : v);
    public static final LongValueParser TIME_DURATION = (v, max) -> TIME.parse(v, max) + "/" + TimeMath.timestamp(max);

    // ASSUMES VALUE IS A TICK COUNT AND PARSE TICKS TO TIME
    public static final LongValueParser TIME_TICK = (v, max) -> TimeMath.timestamp(Maths.tickToMs((int) (v > max ? safePercent(v, max) : v)));
    public static final LongValueParser TIME_DURATION_TICK = (v, max) -> TIME_TICK.parse(v, max) + "/" + TimeMath.timestamp(Maths.tickToMs((int) max));

    private static double safeDivide(double v1, double v2) {
        if (v1 == 0 || v2 == 0) return 0;
        return v1 / v2;
    }

    private static long safePercent(long v1, long v2) {
        if (v1 == 0 || v2 == 0) return 0;
        return v1 % v2;
    }

    private static double round(double value) {
        return value != 0 ? Math.round(value * 100F) / 100F : 0;
    }
}
