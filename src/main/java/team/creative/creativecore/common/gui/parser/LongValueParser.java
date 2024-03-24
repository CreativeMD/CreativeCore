package team.creative.creativecore.common.gui.parser;

import team.creative.creativecore.common.gui.ValueParserUtil;
import team.creative.creativecore.common.util.math.Maths;
import team.creative.creativecore.common.util.math.TimeMath;

@FunctionalInterface
public interface LongValueParser {
    LongValueParser NONE = (v, max) -> v + "";
    LongValueParser TIME = (v, max) -> TimeMath.timestamp((v > max) ? ValueParserUtil.safePercent(v, max) : v);
    LongValueParser TIME_DURATION = (v, max) -> TIME.parse(v, max) + "/" + TimeMath.timestamp(max);

    // ASSUMES VALUE IS A TICK COUNT AND PARSE TICKS TO TIME
    LongValueParser TIME_TICK = (v, max) -> TimeMath.timestamp(Maths.tickToMs((int) (v > max ? ValueParserUtil.safePercent(v, max) : v)));
    LongValueParser TIME_DURATION_TICK = (v, max) -> TIME_TICK.parse(v, max) + "/" + TimeMath.timestamp(Maths.tickToMs((int) max));

    String parse(long v, long max);
}
