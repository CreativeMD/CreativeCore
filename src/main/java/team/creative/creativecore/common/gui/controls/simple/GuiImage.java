package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiIcon;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

public class GuiImage extends GuiControl {
    protected GuiIcon icon;
    protected Color color;
    protected int preferredWidth;
    protected int preferredHeight;

    public GuiImage(String name, int width, int height, GuiIcon icon) {
        super(name);
        this.preferredWidth = width;
        this.preferredHeight = height;
        this.color = Color.WHITE;
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
        return preferredWidth;
    }

    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return preferredHeight;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public StyleDisplay getBackground(GuiStyle style, StyleDisplay display) {
        return StyleDisplay.NONE;
    }

    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.icon.location());
        this.color.glColor();
        GuiRenderHelper.textureRect(graphics.pose(), 0, 0, (int)rect.getWidth(), (int)rect.getHeight(), (float)this.icon.minX(), (float)this.icon.minY(), (float)(this.icon.minX() + this.icon.width()), (float)(this.icon.minY() + this.icon.height()));
        RenderSystem.disableBlend();
    }
}