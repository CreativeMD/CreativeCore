package team.creative.creativecore.common.gui.control.timeline;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;

public class GuiTimelineKey<T> extends GuiControl implements Comparable<GuiTimelineKey> {
    
    public GuiTimelineChannel channel;
    public boolean modifiable = true;
    public int tick;
    public T value;
    
    public GuiTimelineKey(GuiTimelineChannel channel, int tick, T value) {
        super(channel.getParent(), "");
        this.channel = channel;
        this.tick = tick;
        this.value = value;
    }
    
    @Override
    public GuiTimelineKeyDist dist() {
        return (GuiTimelineKeyDist) super.dist();
    }
    
    public void setSelected(boolean selected) {
        dist().setSelected(selected);
    }
    
    public void removeKey() {
        channel.removeKey(this);
    }
    
    @Override
    public int compareTo(GuiTimelineKey o) {
        return Integer.compare(this.tick, o.tick);
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    public static interface GuiTimelineKeyDist extends GuiControlDistHandler {
        
        public void setSelected(boolean selected);
        
    }
}
