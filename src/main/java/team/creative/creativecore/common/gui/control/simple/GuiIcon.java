package team.creative.creativecore.common.gui.control.simple;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.type.Color;

public class GuiIcon extends GuiControl {
    protected Icon icon;
    protected Color shadow;
    protected Color color;
    protected boolean squared;
    
    public GuiIcon(String name, Icon icon) {
        super(name);
        this.icon = icon;
        this.shadow = Color.NONE;
        this.color = Color.WHITE;
    }
    
    public GuiIcon setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }
    
    public GuiIcon setColor(Color color) {
        this.color = color;
        return this;
    }
    
    public GuiIcon setShadow(Color shadowColor) {
        this.shadow = shadowColor;
        return this;
    }
    
    public GuiIcon setSquared(boolean squared) {
        this.squared = squared;
        return this;
    }
    
    @Override
    public GuiIcon setDim(int width, int height) {
        super.setDim(width, height);
        return this;
    }
    
    @Override
    public GuiIcon setDim(GuiSizeRule dim) {
        super.setDim(dim);
        return this;
    }
    
    @Override
    public void init() {
        
    }
    
    @Override
    public void closed() {
        
    }
    
    @Override
    public void tick() {
        
    }
    
    @Override
    public void flowX(int width, int preferred) {
        
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 12;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 12;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public StyleDisplay getBackground(GuiStyle style, StyleDisplay display) {
        return StyleDisplay.NONE;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        
        var location = this.icon.location();
        
        int x = 0, y = 0, width = rect.getContentWidth(), height = rect.getContentHeight();
        if (squared) {
            int size = Math.min(width, height);
            int diff = Math.abs(width - height);
            if (width == size)
                y += diff / 2;
            else
                x += diff / 2;
            width = height = size;
        }
        
        if (this.shadow != Color.NONE) {
            ((CreativeGuiGraphics) graphics).textureRectColor(location, x + 1, y + 1, width, height, this.icon.minX(), this.icon.minY(), this.icon.minX() + this.icon.width(),
                this.icon.minY() + this.icon.height(), shadow.toInt());
        }
        
        ((CreativeGuiGraphics) graphics).textureRectColor(location, x, y, width, height, this.icon.minX(), this.icon.minY(), this.icon.minX() + this.icon.width(), this.icon
                .minY() + this.icon.height(), color.toInt());
        pose.popMatrix();
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
}
