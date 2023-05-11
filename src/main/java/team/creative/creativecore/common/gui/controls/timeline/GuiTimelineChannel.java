package team.creative.creativecore.common.gui.controls.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.list.PairList;

public class GuiTimelineChannel extends GuiParent {
    
    public final GuiTimeline timeline;
    private int cachedHeight;
    public List<GuiTimelineKey> keys = new ArrayList<>();
    private GuiTimelineKey dragged;
    
    public GuiTimelineChannel(GuiTimeline timeline, MutableComponent title) {
        super();
        this.timeline = timeline;
        valign = VAlign.CENTER;
    }
    
    public GuiTimelineChannel addKeyFixed(int tick, double value) {
        GuiTimelineKey key = this.addKey(tick, value);
        key.modifiable = false;
        return this;
    }
    
    public GuiTimelineKey addKey(int tick, double value) {
        GuiTimelineKey key = new GuiTimelineKey(this, tick, value);
        GuiChildControl child = add(key);
        child.setWidth(child.getPreferredWidth(0), 0);
        child.flowX();
        child.setHeight(child.getPreferredHeight(0), 0);
        child.flowY();
        child.setY(cachedHeight / 2 - child.getHeight() / 2);
        timeline.adjustKeyPositionX(key);
        for (int i = 0; i < keys.size(); i++) {
            GuiTimelineKey other = keys.get(i);
            
            if (other.tick == tick)
                return null;
            
            if (other.tick > tick) {
                keys.add(i, key);
                return key;
            }
        }
        keys.add(key);
        return key;
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
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
    
    public void select(GuiTimelineKey key) {
        timeline.selectKey(key);
    }
    
    public void deslect() {
        timeline.deselect();
    }
    
    public void removeKey(GuiTimelineKey key) {
        keys.remove(key);
        remove(key);
    }
    
    public void movedKey(GuiTimelineKey key) {
        Collections.sort(keys);
    }
    
    public void dragKey(GuiTimelineKey key) {
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
    
    public boolean isSpaceFor(@Nullable GuiTimelineKey key, int tick) {
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
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        boolean result = super.mouseClicked(rect, x, y, button);
        if (!result && button == 1) {
            int time = timeline.getTimeAt(x);
            if (isSpaceFor(null, time)) {
                addKey(time, 0);
                playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.1F, 0.6F);
            }
            return true;
        }
        return result;
    }
    
    /*public T getValueAt(int tick) {
        if (keys.isEmpty())
            return getDefault();
        
        int higher = keys.size();
        for (int i = 0; i < keys.size(); i++) {
            int otherTick = keys.get(i).tick;
            if (otherTick == tick)
                return keys.get(i).value;
            if (otherTick > tick) {
                higher = i;
                break;
            }
        }
        
        if (higher == 0 || higher == keys.size())
            return keys.get(higher == 0 ? 0 : keys.size() - 1).value;
        
        GuiTimelineKey<T> before = keys.get(higher - 1);
        GuiTimelineKey<T> after = keys.get(higher);
        double percentage = (double) (tick - before.tick) / (after.tick - before.tick);
        return getValueAt(before, after, percentage);
    }
    
    protected abstract T getValueAt(GuiTimelineKey<T> before, GuiTimelineKey<T> after, double percentage);*/
    
    public PairList<Integer, Double> getPairs() {
        if (controls.isEmpty())
            return null;
        boolean fixed = true;
        PairList<Integer, Double> list = new PairList<>();
        for (GuiTimelineKey control : keys) {
            if (control.modifiable)
                fixed = false;
            list.add(control.tick, control.value);
        }
        if (fixed)
            return null;
        return list;
    }
}
