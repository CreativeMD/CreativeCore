package team.creative.creativecore.common.gui.controls.timeline;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
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
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.math.vec.SmoothValue;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiTimeline extends GuiParent {
    
    protected static final double MAXIMUM_ZOOM = 10;
    
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public StyleDisplay cursorHighlight;
    
    private final GuiRow header;
    private final List<GuiTimelineChannel> channels = new ArrayList<>();
    private final GuiParent sidebar;
    private final GuiParent channelbar;
    private final GuiScrollY channelParent;
    
    private int tick;
    protected int duration = 100;
    protected double basePixelWidth;
    
    protected int headerHeight = 12;
    protected int channelHeight = 9;
    protected int sidebarWidth = 50;
    protected int timelineOffset = 8;
    
    protected SmoothValue zoom = new SmoothValue(100);
    protected SmoothValue scrollX = new SmoothValue(100);
    
    private GuiTimelineKey selected;
    
    private int cachedTimelineWidth;
    private double lastZoom = 0;
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
        channelParent = new GuiScrollY() {
            
            @Override
            public ControlFormatting getControlFormatting() {
                return ControlFormatting.TRANSPARENT;
            }
            
            @Override
            public void scroll(double scrolled) {
                if (Screen.hasControlDown())
                    super.scroll(scrolled);
            }
            
        }.setHovered();
        channelParent.spacing = -1;
        channelParent.align = Align.STRETCH;
        channelParent.flow = GuiFlow.STACK_X;
        add(channelParent);
        sidebar = new GuiParent(GuiFlow.STACK_Y).setAlign(Align.STRETCH);
        sidebar.spacing = -1;
        channelParent.add(sidebar);
        channelbar = new GuiParent(GuiFlow.STACK_Y).setAlign(Align.STRETCH);
        channelbar.spacing = -1;
        channelParent.add(channelbar.setExpandableX());
    }
    
    public GuiTimeline setDuration(int duration) {
        this.duration = duration;
        if (cachedTimelineWidth != 0)
            updateTickWidth();
        adjustKeysPositionX();
        scrollX.setStart(0);
        zoom.setStart(0);
        return this;
    }
    
    private void updateTickWidth() {
        basePixelWidth = (double) (cachedTimelineWidth - timelineOffset * 2) / (double) duration;
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
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        boolean result = super.mouseClicked(rect, x, y, button);
        if (!result && button == 0) {
            deselect();
            return false;
        }
        return result;
    }
    
    public double scrolledX() {
        return scrollX.current();
    }
    
    public void adjustKeyPositionX(GuiTimelineKey key) {
        GuiChildControl child = key.channel.find(key);
        child.setX(timelineOffset + (int) (((GuiTimelineKey) child.control).tick * getTickWidth()) - (child.getWidth() / 2));
    }
    
    public void adjustKeysPositionX() {
        double tickWidth = getTickWidth();
        for (GuiTimelineChannel channel : channels)
            for (GuiChildControl key : channel)
                key.setX(timelineOffset + (int) (((GuiTimelineKey) key.control).tick * tickWidth) - (key.getWidth() / 2));
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
        return Mth.clamp((int) ((x - timelineOffset + tickWidth / 2 + scrollX.current()) / tickWidth), 0, duration);
    }
    
    public int getTimeAtAimed(double x) {
        double tickWidth = getTickWidthAimed();
        return Mth.clamp((int) ((x - timelineOffset + tickWidth / 2 + scrollX.aimed()) / tickWidth), 0, duration);
    }
    
    public int getTime() {
        return tick;
    }
    
    public void setTime(int tick) {
        this.tick = tick;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public void render(PoseStack pose, GuiChildControl control, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        zoom.tick();
        scrollX.tick();
        super.render(pose, control, controlRect, realRect, scale, mouseX, mouseY);
    }
    
    @Override
    public boolean mouseScrolled(Rect rect, double x, double y, double delta) {
        if (Screen.hasShiftDown()) {
            scrollX.set(Mth.clamp(scrollX.aimed() - delta * 10, 0, maxScrollX));
            return true;
        }
        if (Screen.hasControlDown()) {
            channelParent.scroll(delta);
            return true;
        }
        return super.mouseScrolled(rect, x, y, delta);
    }
    
    public void scrolled(int width, double x, double delta) {
        x += timelineOffset;
        int focusedTick = Math.max(0, getTimeAtAimed(x));
        zoom.set(Mth.clamp(zoom.aimed() + delta * basePixelWidth * 2 * Math.max(basePixelWidth * 2, zoom.aimed()) / MAXIMUM_ZOOM, 0, MAXIMUM_ZOOM));
        int currentTick = Math.max(0, getTimeAtAimed(x));
        double aimedTickWidth = getTickWidthAimed();
        
        double sizeX = timelineOffset * 2 + aimedTickWidth * duration;
        maxScrollX = Math.max(0, sizeX - width);
        scrollX.set(Mth.clamp(scrollX.aimed() + (focusedTick - currentTick) * aimedTickWidth, 0, maxScrollX));
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
        
        @Override
        public boolean mouseScrolled(Rect rect, double x, double y, double delta) {
            scrolled((int) rect.getWidth(), x, delta);
            return true;
        }
        
        @Override
        @OnlyIn(Dist.CLIENT)
        @Environment(EnvType.CLIENT)
        protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
            
            if (lastZoom != zoom.current()) {
                lastZoom = zoom.current();
                adjustKeysPositionX();
            }
            
            double tickWidth = getTickWidth();
            
            if (cursorHighlight == null)
                cursorHighlight = new DisplayColor(0.78F, 0.78F, 0, 0.59F);
            
            int width = (int) rect.getWidth();
            int height = control.getHeight();
            int contentOffset = getContentOffset() - 1;
            pose.translate(timelineOffset - 1, -contentOffset, 0);
            
            int ticks = (int) (width / tickWidth);
            int area = 5;
            int halfArea = 5;
            int smallestStep = 0;
            while (Math.pow(area, smallestStep) * tickWidth < 3)
                smallestStep++;
            smallestStep = (int) Math.pow(area, smallestStep);
            
            double stepWidth = tickWidth * smallestStep;
            int stepOffset = (int) (scrollX.current() / stepWidth);
            int stamps = ticks / smallestStep;
            int begin = Math.max(0, stepOffset);
            int end = stepOffset + stamps + 1;
            
            int pointerWidth = Math.max((int) tickWidth, 1);
            cursorHighlight.render(pose, tickWidth * getTime() - pointerWidth / 2D - scrollX.current(), 0, pointerWidth, height);
            
            GuiStyle style = getStyle();
            StyleDisplay border = style.get(ControlStyleBorder.SMALL);
            Font font = GuiRenderHelper.getFont();
            
            pose.pushPose();
            pose.translate(-scrollX.current() + begin * stepWidth, 0, 0);
            for (int i = begin; i < end; i++) {
                if (i % halfArea == 0) {
                    border.render(pose, 1, 4);
                    String text = "" + (i * smallestStep);
                    font.draw(pose, text, 0 - font.width(text) / 2, 5, ColorUtils.BLACK);
                } else
                    border.render(pose, 1, 2);
                
                pose.translate(stepWidth, 0, 0);
            }
            pose.popPose();
        }
        
        @Override
        public void flowX(int width, int preferred) {
            super.flowX(width, preferred);
            cachedTimelineWidth = width;
            updateTickWidth();
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
