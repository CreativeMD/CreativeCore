package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.Icon;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

public class GuiStateIconButton extends GuiControl {
    public final Icon[] states;
    private int index;
    protected Color color;
    protected Color shadow;

    public GuiStateIconButton(String name, Icon... icons) {
        super(name);
        this.states = icons;
        this.color = Color.WHITE;
        this.shadow = Color.BLACK;
    }

    public GuiStateIconButton setState(int index) {
        this.index = index;
        return this;
    }

    public int getState() {
        return this.index;
    }

    public void previousState() {
        int state = this.getState();
        --state;
        if (state < 0) {
            state = this.states.length - 1;
        }

        if (state >= this.states.length) {
            state = 0;
        }

        this.setState(state);
        this.raiseEvent(new GuiControlChangedEvent(this));
    }

    public void nextState() {
        int state = this.getState();
        ++state;
        if (state < 0) {
            state = this.states.length - 1;
        }

        if (state >= this.states.length) {
            state = 0;
        }

        this.setState(state);
        this.raiseEvent(new GuiControlChangedEvent(this));
    }

    public GuiStateIconButton setShadow(Color shadow) {
        this.shadow = shadow;
        return this;
    }

    public GuiStateIconButton setColor(Color color) {
        this.color = color;
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
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.CLICKABLE;
    }

    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        this.nextState();
        return true;
    }

    @Override
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        pose.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, states[index].location());
        if (shadow != null) {
            this.shadow.glColor();
            GuiRenderHelper.textureRect(pose, 1, 1, control.getContentWidth(), control.getContentHeight(), (float)this.states[index].minX(), (float)this.states[index].minY(), (float)(this.states[index].minX() + this.states[index].width()), (float)(this.states[index].minY() + this.states[index].height()));
        }
        this.color.glColor();
        GuiRenderHelper.textureRect(pose, 0, 0, (int)rect.getWidth(), (int)rect.getHeight(), (float)this.states[index].minX(), (float)this.states[index].minY(), (float)(this.states[index].minX() + this.states[index].width()), (float)(this.states[index].minY() + this.states[index].height()));
        RenderSystem.disableBlend();
        pose.popPose();
    }
}
