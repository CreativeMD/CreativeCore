package team.creative.creativecore.client.gui.control.simple;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiLabel.GuiLabelDist;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientLabel<T extends GuiLabel> extends GuiClientControl<T> implements GuiLabelDist {
    
    protected CompiledText text = CompiledText.createAnySize();
    
    public GuiClientLabel(T control) {
        super(control);
    }
    
    @Override
    public void setDefaultColor(int color) {
        text.setDefaultColor(color);
    }
    
    @Override
    public void setDropShadow(boolean shadow) {
        text.setShadow(shadow);
    }
    
    @Override
    public void setAlign(Align align) {
        text.setAlign(align);
    }
    
    @Override
    public void setVAlign(VAlign valgin) {
        text.setVAlign(valgin);
    }
    
    @Override
    public void setTitle(Component component) {
        text.setText(component);
    }
    
    @Override
    public void setTitle(List<Component> components) {
        text.setText(components);
    }
    
    public void setText(CompiledText text) {
        this.text = text;
    }
    
    @Override
    public void setScale(double scale) {
        this.text.setScale(scale);
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    protected void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        text.render(graphics);
    }
    
    @Override
    public void flowX(int width, int preferred) {
        text.setDimension(width, Integer.MAX_VALUE);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        text.setMaxHeight(height);
    }
    
    @Override
    protected int minWidth(int availableWidth) {
        return 0;
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return text.getTotalWidth();
    }
    
    @Override
    protected int minHeight(int width, int availableHeight) {
        return Minecraft.getInstance().font.lineHeight;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return text.getTotalHeight();
    }
    
}
