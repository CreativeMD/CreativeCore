package team.creative.creativecore.common.gui.controls.timeline;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleBorder;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiTimelineKey extends GuiControl implements Comparable<GuiTimelineKey> {
    
    public GuiTimelineChannel channel;
    public boolean modifiable = true;
    public int tick;
    public double value;
    private boolean selected;
    
    public GuiTimelineKey(GuiTimelineChannel channel, int tick, double value) {
        super("");
        this.channel = channel;
        this.tick = tick;
        this.value = value;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        channel.select(this);
        return true;
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
        return 5;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 5;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
    
    @Override
    public void render(PoseStack pose, GuiChildControl control, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        
        RenderSystem.disableScissor();
        GuiStyle style = getStyle();
        
        pose.pushPose();
        double width = controlRect.getWidth();
        double height = controlRect.getHeight();
        
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
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {}
}
