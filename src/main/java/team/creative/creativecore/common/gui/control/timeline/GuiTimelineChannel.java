package team.creative.creativecore.common.gui.control.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public abstract class GuiTimelineChannel<T> extends GuiParent {
    
    public final GuiTimeline timeline;
    public GuiControl sidebarTitle;
    private List<GuiTimelineKey<T>> keys = new ArrayList<>();
    
    public GuiTimelineChannel(GuiTimeline timeline) {
        super(timeline.getParent());
        this.timeline = timeline;
        setVAlign(VAlign.CENTER);
    }
    
    @Override
    public GuiTimelineChannelDist<T> dist() {
        return (GuiTimelineChannelDist<T>) super.dist();
    }
    
    @Override
    public GuiTimelineChannel<T> setTooltip(List<Component> tooltip) {
        super.setTooltip(tooltip);
        sidebarTitle.setTooltip(tooltip);
        return this;
    }
    
    public GuiTimelineChannel<T> addKeyFixed(int tick, T value) {
        GuiTimelineKey key = this.addKey(tick, value);
        key.modifiable = false;
        return this;
    }
    
    public GuiTimelineKey<T> addKey(int tick, T value) {
        GuiTimelineKey<T> key = new GuiTimelineKey<T>(this, tick, value);
        add(key);
        if (hasLayer() && dist() != null)
            dist().keyInitialFlow(key);
        timeline.adjustKeyPositionX(key);
        for (int i = 0; i < keys.size(); i++) {
            GuiTimelineKey<T> other = keys.get(i);
            
            if (other.tick == tick)
                return null;
            
            if (other.tick > tick) {
                keys.add(i, key);
                return key;
            }
        }
        keys.add(key);
        timeline.raiseEvent(new GuiControlChangedEvent(timeline));
        return key;
    }
    
    public void select(GuiTimelineKey<T> key) {
        timeline.selectKey(key);
    }
    
    public void deslect() {
        timeline.deselect();
    }
    
    public void removeChannel() {
        timeline.removeChannel(this);
    }
    
    public void removeKey(GuiTimelineKey<T> key) {
        keys.remove(key);
        remove(key);
        timeline.raiseEvent(new GuiControlChangedEvent(timeline));
    }
    
    public void movedKey(GuiTimelineKey<T> key) {
        Collections.sort(keys);
        timeline.raiseEvent(new GuiControlChangedEvent(timeline));
    }
    
    public void dragKey(GuiTimelineKey<T> key) {
        if (key.modifiable && dist() != null)
            dist().dragKey(key);
    }
    
    public boolean isSpaceFor(@Nullable GuiTimelineKey<T> key, int tick) {
        if (tick < 0 || tick > timeline.duration)
            return false;
        for (int i = 0; i < keys.size(); i++) {
            int otherTick = keys.get(i).tick;
            if (otherTick == tick)
                return false;
            if (otherTick > tick)
                return true;
        }
        return true;
    }
    
    public abstract T getValueAt(int time);
    
    public boolean isChannelEmpty() {
        return keys.isEmpty();
    }
    
    public Iterable<GuiTimelineKey<T>> keys() {
        return keys;
    }
    
    public GuiTimelineKey<T> getFirst() {
        if (keys.isEmpty())
            return null;
        return keys.getFirst();
    }
    
    public GuiTimelineKey<T> getLast() {
        if (keys.isEmpty())
            return null;
        return keys.getLast();
    }
    
    public static interface GuiTimelineChannelDist<K> extends GuiParentDistHandler {
        
        public void keyInitialFlow(GuiTimelineKey<K> key);
        
        public void dragKey(GuiTimelineKey<K> key);
        
    }
    
}
