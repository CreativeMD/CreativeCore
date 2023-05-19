package team.creative.creativecore.common.gui.controls.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public abstract class GuiTimelineChannel<T> extends GuiParent {
    
    public final GuiTimeline timeline;
    public GuiControl sidebarTitle;
    private int cachedHeight;
    private List<GuiTimelineKey<T>> keys = new ArrayList<>();
    private GuiTimelineKey<T> dragged;
    
    public GuiTimelineChannel(GuiTimeline timeline) {
        super();
        this.timeline = timeline;
        valign = VAlign.CENTER;
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
        GuiChildControl child = add(key);
        if (hasLayer()) {
            child.setWidth(child.getPreferredWidth(0), 0);
            child.flowX();
            child.setHeight(child.getPreferredHeight(0), 0);
            child.flowY();
            child.setY((int) Math.ceil(cachedHeight / 2D - child.getHeight() / 2D));
        }
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
    
    @Override
    public void flowX(int width, int preferred) {
        for (GuiChildControl child : controls) {
            child.setWidth(child.getPreferredWidth(0), 0);
            child.flowX();
        }
        timeline.adjustKeysPositionX();
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        super.flowY(width, height, preferred);
        this.cachedHeight = height;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.NESTED;
    }
    
    @Override
    public double getOffsetX() {
        return -timeline.scrolledX();
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
        if (key.modifiable)
            this.dragged = key;
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        if (dragged != null) {
            int tick = Math.max(0, timeline.getTimeAt(x));
            if (dragged.channel.isSpaceFor(dragged, tick)) {
                dragged.tick = tick;
                timeline.adjustKeyPositionX(dragged);
            }
        }
        super.mouseMoved(rect, x, y);
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        if (dragged != null) {
            this.dragged.channel.movedKey(dragged);
            this.dragged = null;
        }
        
        super.mouseReleased(rect, x, y, button);
    }
    
    @Override
    public boolean mouseScrolled(Rect rect, double x, double y, double delta) {
        timeline.scrolled((int) rect.getWidth(), x, delta);
        return true;
    }
    
    public boolean isSpaceFor(@Nullable GuiTimelineKey<T> key, int tick) {
        if (tick > timeline.duration)
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
    
    protected abstract T getValueAt(int time);
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        boolean result = super.mouseClicked(rect, x, y, button);
        if (!result && button == 1) {
            int time = timeline.getTimeAt(x);
            if (isSpaceFor(null, time)) {
                GuiTimelineKey<T> key = addKey(time, getValueAt(time));
                if (key != null) {
                    select(key);
                    playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.1F, 0.6F);
                }
            }
            return true;
        }
        return result;
    }
    
    public boolean isChannelEmpty() {
        return keys.isEmpty();
    }
    
    public Iterable<GuiTimelineKey<T>> keys() {
        return keys;
    }
    
    public GuiTimelineKey<T> getFirst() {
        if (keys.isEmpty())
            return null;
        return keys.get(0);
    }
    
    public GuiTimelineKey<T> getLast() {
        if (keys.isEmpty())
            return null;
        return keys.get(keys.size() - 1);
    }
    
}
