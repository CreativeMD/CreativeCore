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
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

public class GuiIcon extends GuiControl {
    protected Icon icon;
    protected Color color;

    public GuiIcon(String name, int width, int height, Icon icon) {
        super(name);
        this.setDim(width, height);
        this.color = Color.WHITE;
        this.icon = icon;
    }

    public GuiIcon setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public void init() {

    }

    @Override
    public void closed() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void flowX(int i, int i1) {

    }

    @Override
    public void flowY(int i, int i1, int i2) {

    }

    @Override
    protected int preferredWidth(int i) {
        return 12;
    }

    @Override
    protected int preferredHeight(int i, int i1) {
        return 12;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public StyleDisplay getBackground(GuiStyle style, StyleDisplay display) {
        return StyleDisplay.NONE;
    }

    @Override
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {;
        pose.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.icon.location());
        this.color.glColor();
        GuiRenderHelper.textureRect(pose, 0, 0, (int)rect.getWidth(), (int)rect.getHeight(), (float)this.icon.minX(), (float)this.icon.minY(), (float)(this.icon.minX() + this.icon.width()), (float)(this.icon.minY() + this.icon.height()));
        RenderSystem.disableBlend();
        pose.popPose();
    }

    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
}