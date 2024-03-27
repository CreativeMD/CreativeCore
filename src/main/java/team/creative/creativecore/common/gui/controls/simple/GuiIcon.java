package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

public class GuiIcon extends GuiControl {
    protected Icon icon;
    protected Color shadow;
    protected Color color;
protected boolean squared;

    public GuiIcon(String name, Icon icon) {
        super(name);
        this.icon = icon;
        this.shadow = Color.NONE;
        this.color = Color.WHITE;
    }

    public GuiIcon setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }
public GuiIcon setColor(Color color) {
        this.color = color;
        return this;
    }

    public GuiIcon setShadow(Color shadowColor) {
        this.shadow = shadowColor;
        return this;
    }
public GuiIcon setSquared(boolean squared) {
        this.squared = squared;
        return this;
    }
@Override
    public GuiIcon setDim(int width, int height) {
        super.setDim(width, height);
        return this;
    }

    @Override
    public GuiIcon setDim(GuiSizeRule dim) {
        super.setDim(dim);
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
    public void flowX(int width, int preferred) {
    }

    @Override
    public void flowY(int width, int height, int preferred) {
    }

    @Override
    protected int preferredWidth(int availableWidth) {
        return 12;
    }

    @Override
    protected int preferredHeight(int width, int availableHeight) {
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

        int x = 0, y = 0, width = control.getContentWidth(), height = control.getContentHeight();
        if (squared) {
            int size = Math.min(width, height);
            int diff = Math.abs(width - height);
            if (width == size) y += diff / 2;
            else x += diff / 2;
            width = height = size;
        }

        if (this.shadow != Color.NONE) {
            this.shadow.glColor();
            GuiRenderHelper.textureRect(pose, x + 1, y + 1, width, height, (float) this.icon.minX(), (float) this.icon.minY(), (float) (this.icon
                    .minX() + this.icon.width()), (float) (this.icon.minY() + this.icon.height()));
        }

        this.color.glColor();
        GuiRenderHelper.textureRect(pose, x, y, width, height, (float)this.icon.minX(), (float)this.icon.minY(), (float)(this.icon.minX() + this.icon.width()), (float)(this.icon.minY() + this.icon.height()));
        RenderSystem.disableBlend();
        pose.popPose();
    }

    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT_NO_DISABLE;
    }
}
