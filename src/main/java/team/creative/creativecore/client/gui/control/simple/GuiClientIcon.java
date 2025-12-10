package team.creative.creativecore.client.gui.control.simple;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphics;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.control.simple.GuiIcon;
import team.creative.creativecore.common.gui.control.simple.GuiIcon.GuiIconDist;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.type.Color;

public class GuiClientIcon<T extends GuiIcon> extends GuiClientControl<T> implements GuiIconDist {
    
    protected Icon icon;
    protected Color shadow = Color.NONE;
    protected Color color = Color.WHITE;
    protected boolean squared;
    
    public GuiClientIcon(T control) {
        super(control);
    }
    
    @Override
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public void setShadow(Color shadowColor) {
        this.shadow = shadowColor;
    }
    
    @Override
    public void setSquared(boolean squared) {
        this.squared = squared;
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int width, int height, int preferred) {}
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 12;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 12;
    }
    
    @Override
    public StyleDisplay getBackground(GuiStyle style, StyleDisplay display) {
        return StyleDisplay.NONE;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        
        var identifier = this.icon.identifier();
        
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
            ((CreativeGuiGraphics) graphics).textureRectColor(identifier, x + 1, y + 1, width, height, this.icon.minX(), this.icon.minY(), this.icon.minX() + this.icon.width(),
                this.icon.minY() + this.icon.height(), shadow.toInt());
        }
        
        ((CreativeGuiGraphics) graphics).textureRectColor(identifier, x, y, width, height, this.icon.minX(), this.icon.minY(), this.icon.minX() + this.icon.width(), this.icon
                .minY() + this.icon.height(), color.toInt());
        pose.popMatrix();
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
    
}
