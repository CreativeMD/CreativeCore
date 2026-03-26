package team.creative.creativecore.client.gui.control.timeline;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.control.timeline.GuiAnimationHandler;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineHeader;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiClientTimelineHeader<T extends GuiTimelineHeader> extends GuiClientParent<T> {
    
    public boolean dragged = false;
    
    public GuiClientTimelineHeader(T control) {
        super(control);
    }
    
    public GuiClientTimeline timeline() {
        return (GuiClientTimeline) control.timeline.dist();
    }
    
    public GuiAnimationHandler handler() {
        return control.timeline.handler;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        dragged = true;
        handler().set(timeline().getTimeAt(x));
        playSound(SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM);
        return true;
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        if (dragged) {
            int tick = timeline().getTimeAt(x);
            if (tick != handler().get()) {
                handler().set(tick);
                playSound(SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM, 0.02F, 1);
            }
        }
    }
    
    @Override
    public void mouseReleased(double x, double y, MouseButtonInfo info) {
        dragged = false;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        timeline().scrolled(rect.getWidth(), x, delta);
        return true;
    }
    
    @Override
    protected void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        
        if (timeline().getLastZoom() != timeline().zoom.current()) {
            timeline().setLastZoom(timeline().zoom.current());
            timeline().adjustKeysPositionX();
        }
        
        Matrix3x2fStack pose = graphics.pose();
        
        double tickWidth = timeline().getTickWidth();
        
        int width = rect.getContentWidth();
        int height = rect.getContentHeight();
        int contentOffset = getContentOffset() - 1;
        pose.translate(timeline().timelineOffset - 1, -contentOffset);
        
        int ticks = (int) (width / tickWidth);
        int area = 5;
        int halfArea = 5;
        int smallestStep = 0;
        while (Math.pow(area, smallestStep) * tickWidth < 3)
            smallestStep++;
        smallestStep = (int) Math.pow(area, smallestStep);
        
        double stepWidth = tickWidth * smallestStep;
        int stepOffset = (int) (timeline().scrollX.current() / stepWidth);
        int stamps = ticks / smallestStep;
        int begin = Math.max(0, stepOffset);
        int end = stepOffset + stamps + 1;
        
        int pointerWidth = Math.max((int) tickWidth, 1);
        GuiClientTimeline.CURSOR_HIGHLIGHT.render(graphics, tickWidth * handler().get() - pointerWidth / 2D - timeline().scrollX.current(), 0, pointerWidth, height);
        
        GuiStyle style = getStyle();
        StyleDisplay border = style.get(ControlStyleBorder.SMALL);
        Font font = ((CreativeGuiGraphics) graphics).font();
        
        pose.pushMatrix();
        pose.translate((float) (-timeline().scrollX.current() + begin * stepWidth), 0);
        for (int i = begin; i < end; i++) {
            if (i % halfArea == 0) {
                border.render(graphics, 1, 4);
                String text = "" + (i * smallestStep);
                graphics.text(Minecraft.getInstance().font, text, 0 - font.width(text) / 2, 5, ColorUtils.BLACK, false);
            } else
                border.render(graphics, 1, 2);
            
            pose.translate((float) stepWidth, 0);
        }
        pose.popMatrix();
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        timeline().resized(width);
    }
    
}
