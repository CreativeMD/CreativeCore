package team.creative.creativecore.common.gui.controls.simple;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiProgressbar extends GuiControl {
    
    public double pos;
    public double max;
    public boolean showToolTip = true;
    
    public GuiProgressbar(String name, int width, int height, double pos, double max) {
        super(name, width, height);
        this.pos = pos;
        this.max = max;
    }
    
    public GuiProgressbar(String name, double pos, double max) {
        super(name);
        this.pos = pos;
        this.max = max;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public List<Component> getTooltip() {
        if (showToolTip)
            return new TextBuilder().number(pos, true).text("/").number(max, true).text(" (").number(pos / max * 100, true).text("%)").build();
        return super.getTooltip();
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.PROGRESSBAR;
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        GuiStyle style = getStyle();
        double percent = pos / max;
        style.clickable.render(matrix, 0, 0, (int) (rect.getWidth() * percent), rect.getHeight());
        GuiRenderHelper.drawStringCentered(matrix, ((int) Math.round(percent * 100)) + "%", (float) rect.getWidth(), (float) rect.getHeight(), style.fontColor.toInt());
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int height, int preferred) {}
    
    @Override
    protected int preferredWidth() {
        return 40;
    }
    
    @Override
    protected int preferredHeight() {
        return 10;
    }
    
}