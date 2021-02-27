package team.creative.creativecore.common.gui.controls;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiProgressbar extends GuiControlBasic {
    
    public double pos;
    public double max;
    public boolean showToolTip = true;
    
    public GuiProgressbar(String name, int x, int y, int width, int height, double pos, double max) {
        super(name, x, y, width, height);
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
    public List<ITextComponent> getTooltip() {
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
    protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {
        GuiStyle style = getStyle();
        double percent = pos / max;
        style.clickable.render(matrix, 0, 0, (int) (rect.getWidth() * percent), rect.getHeight());
        GuiRenderHelper.drawStringCentered(matrix, ((int) Math.round(percent * 100)) + "%", (float) rect.getWidth(), (float) rect.getHeight(), style.fontColor.toInt(), true);
    }
    
}