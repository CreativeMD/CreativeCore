package team.creative.creativecore.common.gui.controls.timeline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiTimelineKey<T> extends GuiControl implements Comparable<GuiTimelineKey> {
    
    public static final double DRAG_TIME = 2;
    public GuiTimelineChannel channel;
    public boolean modifiable = true;
    public int tick;
    public T value;
    private boolean selected;
    private boolean clicked;
    
    public GuiTimelineKey(GuiTimelineChannel channel, int tick, T value) {
        super("");
        this.channel = channel;
        this.tick = tick;
        this.value = value;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (button == 0) {
            channel.select(this);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            clicked = true;
        } else if (button == 1) {
            if (selected)
                channel.timeline.deselect();
            channel.removeKey(this);
            playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 0.1F, 0.6F);
        }
        return true;
    }
    
    @Override
    public void mouseDragged(Rect rect, double x, double y, int button, double dragX, double dragY, double time) {
        if (clicked && time > DRAG_TIME) {
            channel.dragKey(this);
            clicked = false;
        }
        super.mouseDragged(rect, x, y, button, dragX, dragY, time);
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        clicked = false;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public void removeKey() {
        channel.removeKey(this);
    }
    
    @Override
    public int compareTo(GuiTimelineKey o) {
        return Integer.compare(this.tick, o.tick);
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
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
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
    
    @Override
    public Rect createChildRect(GuiChildControl child, Rect contentRect, double scale, double xOffset, double yOffset) {
        Rect temp = child.rect.copy();
        temp.grow(Math.max(temp.getWidth() / 4, temp.getHeight() / 4));
        return contentRect.child(temp, scale, xOffset, yOffset);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    public void render(GuiGraphics graphics, GuiChildControl control, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        
        PoseStack pose = graphics.pose();
        GuiStyle style = getStyle();
        
        pose.pushPose();
        double width = control.getWidth();
        double height = control.getHeight();
        
        pose.translate(width * 0.5, height * 0.5, 0);
        pose.mulPose(Axis.ZP.rotationDegrees(45));
        pose.translate(width * -0.5, height * -0.5, 0);
        
        int borderWidth = style.getBorder(ControlStyleBorder.SMALL);
        
        style.border.render(pose, width, height);
        
        StyleDisplay foreground = style.clickable;
        if (!enabled || !modifiable)
            foreground = style.disabledBackground;
        else if (selected)
            foreground = style.headerBackground;
        else if (controlRect.inside(mouseX, mouseY))
            foreground = style.clickableHighlight;
        
        foreground.render(pose, borderWidth, borderWidth, width - borderWidth * 2, height - borderWidth * 2);
        pose.popPose();
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    @Environment(EnvType.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {}
}
