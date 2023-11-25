package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.Icons;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.Color;

public class GuiIcon extends GuiControl {
    protected Icons icon;
    protected Color color;

    public GuiIcon(String name) {
        super(name);
        this.preferredWidth = 11;
        this.preferredHeight = 11;
    }

    public GuiIcon setIcon(Icons icon) {
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
    public GuiIcon flowX(int width, int preferred) {
        return this;
    }

    @Override
    public GuiIcon flowY(int height, int preferred) {
        return this;
    }

    @Override
    protected int preferredWidth() {
        return this.preferredWidth;
    }

    @Override
    protected int preferredHeight() {
        return this.preferredHeight;
    }

    @Override
    public ControlFormatting getControlFormatting() {
        return null;
    }

    @Override
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {

    }
}