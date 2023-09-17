package team.creative.creativecore.common.gui.controls.simple;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiLabel extends GuiControl {
    protected float scale = 1.0f;
    protected CompiledText text = CompiledText.createAnySize();
    
    public GuiLabel(String name) {
        super(name);
    }
    
    public GuiLabel(String name, int width, int height) {
        super(name, width, height);
    }
    
    public GuiLabel setDefaultColor(int color) {
        text.defaultColor = color;
        return this;
    }
    
    public GuiLabel setAlign(Align align) {
        text.alignment = align;
        return this;
    }

    public float getScale() {
        return this.scale;
    }

    public GuiLabel setScale(float scale) {
        this.preferredWidth = (int) (this.preferredWidth * scale + 3);
        this.preferredHeight = (int) (this.preferredHeight * scale + 1);
        this.scale = scale;
        return this;
    }
    
    public GuiLabel setTranslate(String translate) {
        return setTitle(new TranslatableComponent(translate));
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
        text.setScale(this.scale);
        text.render(matrix);
    }
    
    @Override
    public void flowX(int width, int preferred) {
        text.setDimension(width, Integer.MAX_VALUE);
    }
    
    @Override
    public void flowY(int height, int preferred) {
        text.setMaxHeight(height);
    }
    
    @Override
    public int getMinWidth() {
        return (int) (10 * scale + 1); // +3 to be aware
    }
    
    @Override
    public int preferredWidth() {
        return (int) (text.getTotalWidth() * scale + 3); // +3 to be aware
    }
    
    @Override
    public int getMinHeight() {
        return (int) (Minecraft.getInstance().font.lineHeight * scale + 1); // +1 to be aware
    }
    
    @Override
    public int preferredHeight() {
        return (int) (text.getTotalHeight() * scale) + 1; // +1 to be aware
    }
    
}