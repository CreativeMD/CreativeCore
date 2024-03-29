package team.creative.creativecore.common.util.math;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeMath {
    private static final DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");
    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String timestamp(long time) {
        if (time < 3600000) {
            long min = TimeUnit.MILLISECONDS.toMinutes(time);
            long sec = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(min);
            return String.format("%02d:%02d", min, sec);
        } else {
            return FORMAT.format(time);
        }
    }
}