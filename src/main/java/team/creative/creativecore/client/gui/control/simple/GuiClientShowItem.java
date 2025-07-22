package team.creative.creativecore.client.gui.control.simple;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphics;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.common.gui.control.simple.GuiShowItem;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientShowItem<T extends GuiShowItem> extends GuiClientControl<T> {
    
    public GuiClientShowItem(T control) {
        super(control);
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int width, int height, int preferred) {}
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 16;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 16;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        float scale = Math.min(rect.getContentWidth() / 16, rect.getContentHeight() / 16);
        Matrix3x2fStack pose = graphics.pose();
        pose.scale(scale, scale);
        graphics.renderItem(control.stack, 0, 0);
    }
    
}
