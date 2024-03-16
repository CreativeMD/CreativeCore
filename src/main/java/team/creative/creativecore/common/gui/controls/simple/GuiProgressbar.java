package team.creative.creativecore.common.gui.controls.simple;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.ValueParsers;
import team.creative.creativecore.common.gui.parser.DoubleValueParser;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiProgressbar extends GuiControl {
    
    public double pos;
    public double max;
    public boolean showToolTip = true;
    public final DoubleValueParser parser;
    
    public GuiProgressbar(String name, double pos, double max) {
        this(name, pos, max, ValueParsers.PERCENT);
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
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        this.renderProgress(pose, control, rect, this.getPercentage());
        GuiRenderHelper.drawStringCentered(pose, parser.parse(pos, max), (float) rect.getWidth(), (float) rect.getHeight(), getStyle().fontColor.toInt(), true);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderProgress(PoseStack pose, GuiChildControl control, Rect rect, double percent) {
        getStyle().clickable.render(pose, 0, 0, (int) (rect.getWidth() * percent), rect.getHeight());
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