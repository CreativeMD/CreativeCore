package team.creative.creativecore.client.gui.control.simple;

import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.control.simple.GuiProgressbar;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiClientProgressbar<T extends GuiProgressbar> extends GuiClientControl<T> {
    
    public GuiClientProgressbar(T control) {
        super(control);
    }
    
    @Override
    public List<Component> getTooltip() {
        if (control.showToolTip)
            return new TextBuilder().number(control.pos, true).text("/").number(control.max, true).text(" (").number(control.pos / control.max * 100, true).text("%)").build();
        return super.getTooltip();
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.PROGRESSBAR;
    }
    
    @Override
    protected void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        this.renderProgress(graphics, control.getPercentage());
        ((CreativeGuiGraphics) graphics).drawStringCentered(control.parser.parse(control.pos, control.max), rect.getContentWidth(), rect.getContentHeight(), getStyle().fontColor
                .toInt(), true);
    }
    
    protected void renderProgress(GuiGraphicsExtractor graphics, double percent) {
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
