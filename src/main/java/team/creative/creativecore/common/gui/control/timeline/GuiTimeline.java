package team.creative.creativecore.common.gui.control.timeline;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.parent.GuiColumn;
import team.creative.creativecore.common.gui.control.parent.GuiColumn.GuiColumnHeader;
import team.creative.creativecore.common.gui.control.parent.GuiRow;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiControlEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.flow.GuiSizeRule.GuiFixedDimension;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiTimeline extends GuiParent {
    
    public final GuiAnimationHandler handler;
    
    private final GuiRow header;
    private final List<GuiTimelineChannel> channels = new ArrayList<>();
    private final GuiParent sidebar;
    private final GuiParent channelbar;
    private final GuiScrollY channelParent;
    
    protected int duration = 100;
    protected int headerHeight = 15;
    protected int channelHeight = 9;
    protected int sidebarWidth = 50;
    
    private GuiTimelineKey selected;
    
    public GuiTimeline(IGuiParent parent, GuiAnimationHandler handler) {
        super(parent);
        this.handler = handler;
        setAlign(Align.STRETCH);
        setVAlign(VAlign.STRETCH);
        setFlow(GuiFlow.STACK_Y);
        setSpacing(-1);
        header = new GuiRow((GuiColumnHeader) new GuiColumnHeader(this).setDim(new GuiFixedDimension(sidebarWidth)), new GuiTimelineHeader(this));
        header.setDim(new GuiSizeRule.GuiSizeRules().prefHeight(headerHeight).minHeight(headerHeight));
        header.setSpacing(-1);
        add(header);
        channelParent = new GuiScrollY(this).setScrollWhenCTRL().setHovered().setFormatting(ControlFormatting.TRANSPARENT);
        channelParent.setSpacing(-1);
        channelParent.setAlign(Align.STRETCH);
        channelParent.setFlow(GuiFlow.STACK_X);
        add(channelParent);
        sidebar = new GuiParent(this, GuiFlow.STACK_Y).setAlign(Align.STRETCH);
        sidebar.setSpacing(-1);
        channelParent.add(sidebar);
        channelbar = new GuiParent(this, GuiFlow.STACK_Y).setAlign(Align.STRETCH);
        channelbar.setSpacing(-1);
        channelParent.add(channelbar.setExpandableX());
    }
    
    public Iterable<GuiTimelineChannel> channels() {
        return channels;
    }
    
    public GuiScrollY getChannelParent() {
        return channelParent;
    }
    
    @Override
    public GuiTimelineDist dist() {
        return (GuiTimelineDist) super.dist();
    }
    
    public int getDuration() {
        return duration;
    }
    
    public GuiTimeline setDuration(int duration) {
        this.duration = duration;
        
        raiseEvent(new GuiControlChangedEvent(this));
        return this;
    }
    
    public void selectKey(GuiTimelineKey key) {
        if (selected != null)
            selected.setSelected(false);
        selected = key;
        selected.setSelected(true);
        raiseEvent(new KeySelectedEvent(key));
    }
    
    public void deselect() {
        if (selected != null) {
            selected.setSelected(false);
            selected = null;
            raiseEvent(new NoKeySelectedEvent(this));
        }
    }
    
    public void adjustKeyPositionX(GuiTimelineKey key) {
        dist().adjustKeyPositionX(key);
    }
    
    public void adjustKeysPositionX() {
        dist().adjustKeysPositionX();
    }
    
    public void setSidebarWidth(int sidebarWidth) {
        this.sidebarWidth = sidebarWidth;
        this.sidebar.setDim(sidebarWidth, -1);
    }
    
    public GuiTimelineChannel addGuiTimelineChannel(MutableComponent title, GuiTimelineChannel channel) {
        GuiColumn left = new GuiColumnHeader(this);
        left.add(new GuiLabel(this, "title").setDropShadow(false).setDefaultColor(ColorUtils.BLACK).setTitle(title.withStyle(ChatFormatting.BOLD)));
        sidebar.add(left.setVAlign(VAlign.CENTER).setDim(sidebarWidth, channelHeight));
        channel.sidebarTitle = left;
        channels.add(channel);
        channelbar.add(channel.setDim(-1, channelHeight));
        return channel;
    }
    
    public void removeChannel(GuiTimelineChannel channel) {
        sidebar.remove(channel.sidebarTitle);
        channelbar.remove(channel);
        channels.remove(channel);
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    @Override
    public boolean isExpandableX() {
        return dist().isExpandableX();
    }
    
    public static class KeySelectedEvent extends GuiControlEvent<GuiTimelineKey> {
        
        public KeySelectedEvent(GuiTimelineKey source) {
            super(source);
        }
        
        @Override
        public boolean cancelable() {
            return false;
        }
        
    }
    
    public static class NoKeySelectedEvent extends GuiControlEvent<GuiTimeline> {
        
        public NoKeySelectedEvent(GuiTimeline timeline) {
            super(timeline);
        }
        
        @Override
        public boolean cancelable() {
            return false;
        }
        
    }
    
    public static interface GuiTimelineDist extends GuiParentDistHandler {
        
        public void afterDurationSet();
        
        public void adjustKeyPositionX(GuiTimelineKey key);
        
        public void adjustKeysPositionX();
    }
    
}
