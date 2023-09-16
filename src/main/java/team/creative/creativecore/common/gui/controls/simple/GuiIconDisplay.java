package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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

public class GuiIconDisplay extends GuiControl {
    protected GuiIcon icon;
    protected Color color;

    public GuiIconDisplay(String name, int width, int height, GuiIcon icon) {
        super(name, width, height);
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
    public void flowX(int i, int i1) {}

    @Override
    public void flowY(int i, int i1) {}

    protected int preferredWidth() { return 16; }
    protected int preferredHeight() { return 16; }

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
    protected void renderContent(PoseStack pose, GuiChildControl guiChildControl, Rect rect, int i, int i1) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.icon.location());
        this.color.glColor();
        RenderSystem.enableTexture();
        GuiRenderHelper.textureRect(pose, 0, 0, (int)rect.getWidth(), (int)rect.getHeight(), (float)this.icon.minX(), (float)this.icon.minY(), (float)(this.icon.minX() + this.icon.width()), (float)(this.icon.minY() + this.icon.height()));
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}