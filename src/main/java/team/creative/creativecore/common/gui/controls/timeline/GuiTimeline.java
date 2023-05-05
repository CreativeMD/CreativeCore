package team.creative.creativecore.common.gui.controls.timeline;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn.GuiColumnHeader;
import team.creative.creativecore.common.gui.controls.parent.GuiRow;
import team.creative.creativecore.common.gui.controls.parent.GuiScrollY;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.event.GuiControlEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.flow.GuiSizeRule.GuiFixedDimension;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.math.vec.SmoothValue;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiTimeline extends GuiParent {
    
    protected static final double MAXIMUM_ZOOM = 10;
    
    private final GuiRow header;
    private final List<GuiTimelineChannel> channels = new ArrayList<>();
    private final GuiParent sidebar;
    private final GuiParent channelbar;
    
    private int tick;
    protected int duration = 100;
    protected double ticksPerPixel;
    protected double basePixelWidth;
    
    protected int headerHeight = 11;
    protected int channelHeight = 10;
    protected int sidebarWidth = 50;
    
    protected SmoothValue zoom = new SmoothValue(100);
    protected SmoothValue scrollX = new SmoothValue(100);
    
    private GuiTimelineKey selected;
    
    private double lastZoom = 0;
    protected double sizeX;
    protected double maxScrollX;
    
    public GuiTimeline() {
        align = Align.STRETCH;
        valign = VAlign.STRETCH;
        flow = GuiFlow.STACK_Y;
        spacing = -1;
        header = new GuiRow((GuiColumnHeader) new GuiColumnHeader().setDim(new GuiFixedDimension(sidebarWidth)), new GuiTimelineHeader());
        header.setDim(new GuiSizeRule.GuiSizeRules().prefHeight(14));
        header.spacing = -1;
        add(header);
        GuiScrollY channelParent = new GuiScrollY() {
            
            @Override
            public ControlFormatting getControlFormatting() {
                return ControlFormatting.TRANSPARENT;
            }
        }.setHovered();
        channelParent.spacing = -1;
        channelParent.align = Align.STRETCH;
        channelParent.flow = GuiFlow.STACK_X;
        add(channelParent);
        sidebar = new GuiParent(GuiFlow.STACK_Y).setAlign(Align.STRETCH);
        channelParent.add(sidebar);
        channelbar = new GuiParent(GuiFlow.STACK_Y).setAlign(Align.STRETCH);
        channelParent.add(channelbar.setExpandableX());
    }
    
    public GuiTimeline setDuration(int duration) {
        this.duration = duration;
        adjustKeysPositionX();
        scrollX.setStart(0);
        zoom.setStart(0);
        return this;
    }
    
    public void selectKey(GuiTimelineKey key) {
        if (selected != null)
            selected.setSelected(false);
        selected = key;
        selected.setSelected(true);
        raiseEvent(new KeySelectedEvent(key));
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        boolean result = super.mouseClicked(rect, x, y, button);
        if (!result && button == 0) {
            if (selected != null) {
                selected.setSelected(false);
                selected = null;
                raiseEvent(new NoKeySelectedEvent(this));
            }
            return false;
        }
        return result;
    }
    
    public double scrolledX() {
        return scrollX.current();
    }
    
    public void adjustKeyPositionX(GuiTimelineKey key) {
        GuiChildControl child = key.channel.find(key);
        child.setX((int) (((GuiTimelineKey) child.control).tick * getTickWidth()) - (child.getWidth() / 2));
    }
    
    public void adjustKeysPositionX() {
        double tickWidth = getTickWidth();
        for (GuiTimelineChannel channel : channels)
            for (GuiChildControl key : channel)
                key.setX((int) (((GuiTimelineKey) key.control).tick * tickWidth) - (key.getWidth() / 2));
    }
    
    public void setSidebarWidth(int sidebarWidth) {
        this.sidebarWidth = sidebarWidth;
        this.sidebar.setDim(sidebarWidth, -1);
    }
    
    public GuiTimelineChannel addGuiTimelineChannel(MutableComponent title) {
        GuiColumn left = new GuiColumnHeader();
        left.add(new GuiLabel("title").setDropShadow(false).setDefaultColor(ColorUtils.BLACK).setTitle(title.withStyle(ChatFormatting.BOLD)));
        sidebar.add(left.setVAlign(VAlign.CENTER).setDim(sidebarWidth, channelHeight));
        GuiTimelineChannel channel = new GuiTimelineChannel(this, title);
        channels.add(channel);
        channelbar.add(channel.setDim(-1, channelHeight));
        return channel;
    }
    
    @Override
    public boolean isExpandableX() {
        return expandableX;
    }
    
    protected double getTickWidth() {
        return basePixelWidth + zoom.current();
    }
    
    protected double getTickWidthAimed() {
        return basePixelWidth + zoom.aimed();
    }
    
    public int getTimeAt(double x) {
        double tickWidth = getTickWidth();
        return Mth.clamp((int) ((x + tickWidth / 2 + scrollX.current()) / tickWidth), 0, duration);
    }
    
    public int getTimeAtAimed(double x) {
        double tickWidth = getTickWidthAimed();
        return Mth.clamp((int) ((x + tickWidth / 2 + scrollX.current()) / tickWidth), 0, duration);
    }
    
    public int getTime() {
        return tick;
    }
    
    public void setTime(int tick) {
        this.tick = tick;
        // TODO SET TIME
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        ticksPerPixel = (double) duration / width;
        basePixelWidth = 1D / ticksPerPixel;
    }
    
    public class GuiTimelineHeader extends GuiColumnHeader {
        
        public boolean dragged = false;
        
        public GuiTimelineHeader() {
            setExpandableX();
        }
        
        @Override
        public boolean mouseClicked(Rect rect, double x, double y, int button) {
            dragged = true;
            setTime(getTimeAt(x));
            playSound(SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM);
            return true;
        }
        
        @Override
        public void mouseMoved(Rect rect, double x, double y) {
            if (dragged) {
                int tick = getTimeAt(x);
                if (tick != getTime()) {
                    setTime(tick);
                    playSound(SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM, 0.02F, 1);
                }
            }
        }
        
        @Override
        public void mouseReleased(Rect rect, double x, double y, int button) {
            dragged = false;
        }
        
    }
    
    public static class KeySelectedEvent extends GuiControlEvent {
        
        public KeySelectedEvent(GuiTimelineKey source) {
            super(source);
        }
        
        @Override
        public boolean cancelable() {
            return false;
        }
        
    }
    
    public static class NoKeySelectedEvent extends GuiControlEvent {
        
        public NoKeySelectedEvent(GuiTimeline timeline) {
            super(timeline);
        }
        
        @Override
        public boolean cancelable() {
            return false;
        }
        
    }
    
}
