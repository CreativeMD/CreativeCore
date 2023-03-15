package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiIcon;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

public class GuiIconButton extends GuiControl {
    
    protected Consumer<Integer> pressed;
    protected GuiIcon icon;
    protected Color color = Color.WHITE;
    protected Color shadow = Color.BLACK;
    
    public GuiIconButton(String name, GuiIcon icon, Consumer<Integer> pressed) {
        super(name);
        setDim(12, 12);
        this.icon = icon;
        this.pressed = pressed;
    }
    
    public GuiIconButton(String name, int width, int height, GuiIcon icon, Consumer<Integer> pressed) {
        super(name);
        setDim(width, height);
        this.icon = icon;
        this.pressed = pressed;
    }
    
    public void setIcon(GuiIcon icon) {
        this.icon = icon;
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
        return 12;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 12;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        if (pressed != null)
            pressed.accept(button);
        return true;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, icon.location());
        
        shadow.glColor();
        GuiRenderHelper
                .textureRect(pose, 1, 1, control.getContentWidth(), control.getContentHeight(), icon.minX(), icon.minY(), icon.minX() + icon.width(), icon.minY() + icon.height());
        
        color.glColor();
        GuiRenderHelper
                .textureRect(pose, 0, 0, control.getContentWidth(), control.getContentHeight(), icon.minX(), icon.minY(), icon.minX() + icon.width(), icon.minY() + icon.height());
        
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }
}
