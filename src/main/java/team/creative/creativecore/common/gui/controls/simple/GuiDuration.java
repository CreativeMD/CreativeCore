package team.creative.creativecore.common.gui.controls.simple;

import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;

public class GuiDuration extends GuiParent {
    
    public static final int TICKS_PER_SECOND = 20;
    public static final int TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    public static final int TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;
    public static final int TICKS_PER_DAY = TICKS_PER_HOUR * 24;
    
    private GuiCounter d;
    private GuiCounter h;
    private GuiCounter m;
    private GuiCounter s;
    
    public GuiDuration(String name, int ticks, boolean showDays, boolean showHours, boolean showMinutes, boolean showSeconds) {
        super(name);
        setVAlign(VAlign.CENTER);
        if (showDays) {
            add(d = new GuiCounter("d", 0, 0, Integer.MAX_VALUE));
            add(new GuiLabel("dLabel").setTranslate("gui.day"));
        }
        if (showHours) {
            add(h = new GuiCounter("h", 0, 0, Integer.MAX_VALUE));
            add(new GuiLabel("hLabel").setTranslate("gui.hour"));
        }
        if (showMinutes) {
            add(m = new GuiCounter("m", 0, 0, Integer.MAX_VALUE));
            add(new GuiLabel("mLabel").setTranslate("gui.minute"));
        }
        if (showSeconds) {
            add(s = new GuiCounter("s", 0, 0, Integer.MAX_VALUE));
            add(new GuiLabel("sLabel").setTranslate("gui.second"));
        }
        setDuration(ticks);
    }
    
    public void setDuration(int ticks) {
        if (d != null) {
            d.setValue(ticks / TICKS_PER_DAY);
            ticks -= d.getValue() * TICKS_PER_DAY;
        }
        if (h != null) {
            h.setValue(ticks / TICKS_PER_HOUR);
            ticks -= h.getValue() * TICKS_PER_HOUR;
        }
        if (m != null) {
            m.setValue(ticks / TICKS_PER_MINUTE);
            ticks -= m.getValue() * TICKS_PER_MINUTE;
        }
        if (s != null) {
            s.setValue(ticks / TICKS_PER_SECOND);
            ticks -= s.getValue() * TICKS_PER_SECOND;
        }
    }
    
    public int getDuration() {
        int duration = 0;
        if (d != null)
            duration += d.getValue() * TICKS_PER_DAY;
        if (h != null)
            duration += h.getValue() * TICKS_PER_HOUR;
        if (m != null)
            duration += m.getValue() * TICKS_PER_MINUTE;
        if (s != null)
            duration += s.getValue() * TICKS_PER_SECOND;
        return duration;
    }
    
}
