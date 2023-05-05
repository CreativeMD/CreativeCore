package team.creative.creativecore.common.gui.controls.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.MutableComponent;
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
        return timeline.scrolledX();
    }
    
    public void select(GuiTimelineKey key) {
        timeline.selectKey(key);
    }
    
    public void removeKey(GuiTimelineKey key) {
        keys.remove(key);
        remove(key);
    }
    
    public void movedKey(GuiTimelineKey key) {
        Collections.sort(keys);
    }
    
    public boolean isSpaceFor(GuiTimelineKey key, int tick) {
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
        if (!super.mouseClicked(rect, x, y, button) && button == 1) {
            addKey(timeline.getTimeAt(x), 0);
            return true;
        }
        return false;
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
