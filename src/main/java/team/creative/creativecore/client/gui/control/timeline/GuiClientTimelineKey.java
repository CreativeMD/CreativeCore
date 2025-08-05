package team.creative.creativecore.client.gui.control.timeline;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineKey;
import team.creative.creativecore.common.gui.control.timeline.GuiTimelineKey.GuiTimelineKeyDist;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiClientTimelineKey<T extends GuiTimelineKey<K>, K> extends GuiClientControl<T> implements GuiTimelineKeyDist {
    
    public static final double DRAG_TIME = 2;
    private boolean selected;
    private boolean clicked;
    
    public GuiClientTimelineKey(T control) {
        super(control);
    }
    
    @Override
    public Rect createChildRect(Rect contentRect, double scale, double xOffset, double yOffset) {
        Rect temp = rect.rectCopy();
        temp.grow(Math.max(temp.getWidth() / 4, temp.getHeight() / 4));
        return contentRect.child(temp, scale, xOffset, yOffset);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            control.channel.select(control);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            clicked = true;
        } else if (button == 1) {
            if (selected)
                control.channel.timeline.deselect();
            control.channel.removeKey(control);
            playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 0.1F, 0.6F);
        }
        return true;
    }
    
    @Override
    public void mouseDragged(double x, double y, int button, double dragX, double dragY, double time) {
        if (clicked && time > DRAG_TIME) {
            control.channel.dragKey(control);
            clicked = false;
        }
        super.mouseDragged(x, y, button, dragX, dragY, time);
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        clicked = false;
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int width, int height, int preferred) {}
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 6;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 6;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
    
    @Override
    public void render(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(Minecraft.getInstance().getMainRenderTarget().getDepthTexture(), 1.0);
        
        Matrix3x2fStack pose = graphics.pose();
        GuiStyle style = getStyle();
        
        pose.pushMatrix();
        int width = rect.getWidth();
        int height = rect.getHeight();
        
        pose.translate(width * 0.5F, height * 0.5F);
        pose.rotate(45);
        pose.translate(width * -0.5F, height * -0.5F);
        
        int borderWidth = style.getBorder(ControlStyleBorder.SMALL);
        
        style.border.render(graphics, width, height);
        
        StyleDisplay foreground = style.clickable;
        if (!enabled || !control.modifiable)
            foreground = style.disabledBackground;
        else if (selected)
            foreground = style.headerBackground;
        else if (controlRect.inside(mouseX, mouseY))
            foreground = style.clickableHighlight;
        
        foreground.render(graphics, borderWidth, borderWidth, width - borderWidth * 2, height - borderWidth * 2);
        pose.popMatrix();
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {}
    
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
}
