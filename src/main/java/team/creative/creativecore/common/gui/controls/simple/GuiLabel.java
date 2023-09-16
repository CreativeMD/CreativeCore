package team.creative.creativecore.common.gui.controls.simple;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    protected final float scale;
    protected CompiledText text = CompiledText.createAnySize();
    
    public GuiLabel(String name) {
        this(name, 1.0f);
    }

    public GuiLabel(String name, float scale) {
        super(name);
        this.scale = scale;
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
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        text.setScale(scale);
        text.render(graphics.pose());
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
        return (int) (text.getTotalWidth() * scale) + 3;  // +3 due some various font symbol/spacing width
    }
    
    @Override
    protected int minHeight(int width, int availableHeight) {
        return (int) (Minecraft.getInstance().font.lineHeight * scale);
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return (int) (text.getTotalHeight() * scale);
    }
    
}