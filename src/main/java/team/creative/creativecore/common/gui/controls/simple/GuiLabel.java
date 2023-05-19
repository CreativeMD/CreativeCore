package team.creative.creativecore.common.gui.controls.simple;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiLabel extends GuiControl {
    
    protected CompiledText text = CompiledText.createAnySize();
    
    public GuiLabel(String name) {
        super(name);
    }
    
    public GuiLabel setDefaultColor(int color) {
        text.setDefaultColor(color);
        return this;
    }
    
    public GuiLabel setDropShadow(boolean shadow) {
        text.setShadow(shadow);
        return this;
    }
    
    public GuiLabel setAlign(Align align) {
        text.setAlign(align);
        return this;
    }
    
    public GuiLabel setVAlign(VAlign valgin) {
        text.setVAlign(valgin);
        return this;
    }
    
    public GuiLabel setTranslate(String translate) {
        return setTitle(translatable(translate));
    }
    
    public GuiLabel setTranslate(String translate, Object... params) {
        return setTitle(translatable(translate, params));
    }
    
    public GuiLabel setTitle(Component component) {
        text.setText(component);
        if (hasGui())
            reflow();
        return this;
    }
    
    public GuiLabel setTitle(List<Component> components) {
        text.setText(components);
        if (hasGui())
            reflow();
        return this;
    }
    
    public GuiLabel setText(CompiledText text) {
        this.text = text;
        if (hasGui())
            reflow();
        return this;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        text.render(matrix);
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
