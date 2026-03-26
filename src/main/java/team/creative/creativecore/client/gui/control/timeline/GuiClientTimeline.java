package team.creative.creativecore.client.gui.control.timeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.util.Mth;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.client.gui.control.parent.GuiClientScrollY;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.control.timeline.GuiTimeline;
import team.creative.creativecore.common.gui.control.timeline.GuiTimeline.GuiTimelineDist;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineChannel;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineKey;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.math.vec.SmoothValue;

public class GuiClientTimeline<T extends GuiTimeline> extends GuiClientParent<T> implements GuiTimelineDist {
    
    protected static final double MAXIMUM_ZOOM = 10;
    
    public static final StyleDisplay CURSOR_HIGHLIGHT = new DisplayColor(0.78F, 0.78F, 0, 0.59F);
    
    protected double basePixelWidth;
    
    protected SmoothValue zoom = new SmoothValue(100);
    protected SmoothValue scrollX = new SmoothValue(100);
    
    private int cachedTimelineWidth;
    private double lastZoom = 0;
    protected double maxScrollX;
    protected int timelineOffset = 8;
    
    public GuiClientTimeline(T control) {
        super(control);
    }
    
    public double scrolledX() {
        return scrollX.current();
    }
    
    @Override
    public void afterDurationSet() {
        if (cachedTimelineWidth != 0)
            updateTickWidth();
        adjustKeysPositionX();
        scrollX.setStart(0);
        zoom.setStart(0);
    }
    
    public void resized(int width) {
        cachedTimelineWidth = width;
        updateTickWidth();
    }
    
    private void updateTickWidth() {
        basePixelWidth = (double) (cachedTimelineWidth - timelineOffset * 2) / control.getDuration();
    }
    
    public void setLastZoom(double zoom) {
        this.lastZoom = zoom;
    }
    
    public double getLastZoom() {
        return lastZoom;
    }
    
    @Override
    public void adjustKeyPositionX(GuiTimelineKey key) {
        var rect = ((GuiClientControl) key.dist()).rect;
        rect.setX(timelineOffset + (int) (key.tick * getTickWidth()) - (rect.getWidth() / 2));
    }
    
    @Override
    public void adjustKeysPositionX() {
        double tickWidth = getTickWidth();
        for (GuiTimelineChannel<?> channel : control.channels())
            for (GuiControl key : channel) {
                var rect = ((GuiClientControl) key.dist()).rect;
                rect.setX(timelineOffset + (int) (((GuiTimelineKey) key).tick * tickWidth) - (rect.getWidth() / 2));
            }
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        boolean result = super.mouseClicked(x, y, info);
        if (!result && info.button() == 0) {
            control.deselect();
            return false;
        }
        return result;
    }
    
    @Override
    public void render(GuiGraphicsExtractor graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        zoom.tick();
        scrollX.tick();
        super.render(graphics, controlRect, realRect, scale, mouseX, mouseY);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (Minecraft.getInstance().hasShiftDown()) {
            scrollX.set(Mth.clamp(scrollX.aimed() - delta * 10, 0, maxScrollX));
            return true;
        }
        if (Minecraft.getInstance().hasControlDown()) {
            ((GuiClientScrollY) control.getChannelParent().dist()).scroll(delta);
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }
    
    public void scrolled(int width, double x, double delta) {
        x += timelineOffset;
        int focusedTick = Math.max(0, getTimeAtAimed(x));
        zoom.set(Mth.clamp(zoom.aimed() + delta * basePixelWidth * 2 * Math.max(basePixelWidth * 2, zoom.aimed()) / MAXIMUM_ZOOM, 0, MAXIMUM_ZOOM));
        int currentTick = Math.max(0, getTimeAtAimed(x));
        double aimedTickWidth = getTickWidthAimed();
        
        double sizeX = timelineOffset * 2 + aimedTickWidth * control.getDuration();
        maxScrollX = Math.max(0, sizeX - width);
        scrollX.set(Mth.clamp(scrollX.aimed() + (focusedTick - currentTick) * aimedTickWidth, 0, maxScrollX));
    }
    
    protected double getTickWidth() {
        return basePixelWidth + zoom.current();
    }
    
    protected double getTickWidthAimed() {
        return basePixelWidth + zoom.aimed();
    }
    
    public int getTimeAt(double x) {
        double tickWidth = getTickWidth();
        return Mth.clamp((int) ((x - timelineOffset + tickWidth / 2 + scrollX.current()) / tickWidth), 0, control.getDuration());
    }
    
    public int getTimeAtAimed(double x) {
        double tickWidth = getTickWidthAimed();
        return Mth.clamp((int) ((x - timelineOffset + tickWidth / 2 + scrollX.aimed()) / tickWidth), 0, control.getDuration());
    }
}
