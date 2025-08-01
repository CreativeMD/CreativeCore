package team.creative.creativecore.client.gui.control.timeline;

import org.checkerframework.checker.units.qual.K;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineChannel;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineChannel.GuiTimelineChannelDist;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineKey;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientTimelineChannel<T extends GuiTimelineChannel<K>> extends GuiClientParent<T> implements GuiTimelineChannelDist<K> {
    
    private GuiTimelineKey<T> dragged;
    
    public GuiClientTimelineChannel(T control) {
        super(control);
    }
    
    public GuiClientTimeline timeline() {
        return (GuiClientTimeline) control.timeline.dist();
    }
    
    @Override
    public void keyInitialFlow(GuiTimelineKey<K> key) {
        var rect = ((GuiClientControl) key.dist()).rect;
        rect.setWidth(rect.getPreferredWidth(0), 0);
        rect.flowX();
        rect.setHeight(rect.getPreferredHeight(0), 0);
        rect.flowY();
        rect.setY((int) Math.ceil(this.rect.getContentHeight() / 2D - rect.getHeight() / 2D));
    }
    
    @Override
    public void dragKey(GuiTimelineKey key) {
        this.dragged = key;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        for (GuiClientControl control : controls()) {
            control.rect.setWidth(control.rect.getPreferredWidth(0), 0);
            control.rect.flowX();
        }
        control.timeline.adjustKeysPositionX();
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.NESTED;
    }
    
    @Override
    public double getOffsetX() {
        return -timeline().scrolledX();
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        if (dragged != null) {
            int tick = Math.max(0, timeline().getTimeAt(x));
            if (dragged.channel.isSpaceFor(dragged, tick)) {
                dragged.tick = tick;
                control.timeline.adjustKeyPositionX(dragged);
            }
        }
        super.mouseMoved(x, y);
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        if (dragged != null) {
            this.dragged.channel.movedKey(dragged);
            this.dragged = null;
        }
        
        super.mouseReleased(x, y, button);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        timeline().scrolled(rect.getWidth(), x, delta);
        return true;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean result = super.mouseClicked(x, y, button);
        if (!result && button == 1) {
            int time = timeline().getTimeAt(x);
            if (control.isSpaceFor(null, time)) {
                GuiTimelineKey<K> key = control.addKey(time, control.getValueAt(time));
                if (key != null) {
                    control.select(key);
                    playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.1F, 0.6F);
                }
            }
            return true;
        }
        return result;
    }
}
