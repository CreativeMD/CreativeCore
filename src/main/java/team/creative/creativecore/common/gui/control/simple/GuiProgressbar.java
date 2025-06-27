package team.creative.creativecore.common.gui.control.simple;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.parser.DoubleValueParser;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiProgressbar extends GuiControl {
    
    public double pos;
    public double max;
    public boolean showToolTip = true;
    public final DoubleValueParser parser;
    
    public GuiProgressbar(String name, double pos, double max) {
        this(name, pos, max, DoubleValueParser.PERCENT);
    }
    
    public GuiProgressbar(String name, double pos, double max, DoubleValueParser valueParser) {
        super(name);
        this.pos = pos;
        this.max = max;
        this.parser = valueParser;
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
    
    public double getPercentage() {
        return this.pos / this.max;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.PROGRESSBAR;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        this.renderProgress(graphics, this.getPercentage());
        ((CreativeGuiGraphics) graphics).drawStringCentered(parser.parse(pos, max), rect.getContentWidth(), rect.getContentHeight(), getStyle().fontColor.toInt(), true);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderProgress(GuiGraphics graphics, double percent) {
        getStyle().clickable.render(graphics, 0, 0, (int) (rect.getContentWidth() * percent), rect.getContentHeight());
    }
    
    @Override
    public void flowX(int width, int preferred) {}
    
    @Override
    public void flowY(int width, int height, int preferred) {}
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return 40;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return 10;
    }
    
}