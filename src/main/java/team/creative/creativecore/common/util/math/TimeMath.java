package team.creative.creativecore.common.util.math;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class TimeMath {
    public static String timestamp(long time) {
        if (time < 3600000) {
            long min = TimeUnit.MILLISECONDS.toMinutes(time);
            long sec = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(min);
            return String.format("%02d:%02d", min, sec);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(time);
        }
    }
}