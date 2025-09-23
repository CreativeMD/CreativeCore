package team.creative.creativecore.client.gui.control.simple;

import net.minecraft.client.gui.GuiGraphics;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.common.gui.control.simple.GuiColorPlate;
import team.creative.creativecore.common.gui.control.simple.GuiColorPlate.GuiColorPlateDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.display.DisplayColor;
import team.creative.creativecore.common.util.type.Color;

public class GuiClientColorPlate<T extends GuiColorPlate> extends GuiClientControl<T> implements GuiColorPlateDist {
    
    private Color color;
    private DisplayColor colorPlate;
    
    public GuiClientColorPlate(T control) {
        super(control);
    }
    
    @Override
    public void setColor(Color color, boolean notify) {
        this.color = color;
        this.colorPlate = new DisplayColor(color);
        if (notify)
            raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.NESTED_NO_PADDING;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        getStyle().transparencyBackground.render(graphics, rect.getContentWidth(), rect.getContentHeight());
        colorPlate.set(color);
        colorPlate.render(graphics, rect.getContentWidth(), rect.getContentHeight());
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int width, int height, int preferred) {}
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 20;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 20;
    }
}
