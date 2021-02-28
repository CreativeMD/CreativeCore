package team.creative.creativecore.common.gui.controls;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiLabel extends GuiControl {
    
    protected CompiledText text;
    public Align align;
    public int color = ColorUtils.WHITE;
    
    public GuiLabel(String name, int x, int y) {
        super(name, x, y, 1, 1);
        if (text == null)
            text = create();
    }
    
    protected CompiledText create() {
        return new CompiledText(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    protected void updateDimension() {
        if (getParent() != null) {
            setWidth(getPreferredWidth());
            setHeight(getPreferredHeight());
        }
    }
    
    public GuiLabel setTitle(ITextComponent component) {
        text.setText(component);
        if (getParent() != null)
            initiateLayoutUpdate();
        return this;
    }
    
    public GuiLabel setTitle(List<ITextComponent> components) {
        text.setText(components);
        if (getParent() != null)
            initiateLayoutUpdate();
        return this;
    }
    
    @Override
    public void init() {
        updateDimension();
    }
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
        text.render(matrix);
    }
    
    @Override
    public void setWidthLayout(int width) {
        int offset = getContentOffset();
        text.setDimension(width - offset * 2, Integer.MAX_VALUE);
        text.calculateDimensions();
        setWidth(text.usedWidth + offset * 2);
        setHeight(text.usedHeight + offset * 2);
    }
    
    @Override
    public int getMinWidth() {
        return 10;
    }
    
    @Override
    public int getPreferredWidth() {
        return text.getTotalWidth() + getContentOffset() * 2;
    }
    
    @Override
    public void setHeightLayout(int height) {
        text.setMaxHeight(height - getContentOffset() * 2);
        setHeight(height);
    }
    
    @Override
    public int getMinHeight() {
        return Minecraft.getInstance().fontRenderer.FONT_HEIGHT + getContentOffset() * 2;
    }
    
    @Override
    public int getPreferredHeight() {
        return text.getTotalHeight() + getContentOffset() * 2;
    }
    
}
