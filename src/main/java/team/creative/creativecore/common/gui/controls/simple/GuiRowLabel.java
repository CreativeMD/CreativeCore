package team.creative.creativecore.common.gui.controls.simple;

import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.text.CompiledText;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiRowLabel extends GuiLabel {
    
    public final int index;
    public final Consumer<Integer> consumer;
    public final boolean selected;
    
    public GuiRowLabel(String name, int index, boolean selected, Consumer<Integer> consumer) {
        super(name);
        this.index = index;
        this.consumer = consumer;
        this.selected = selected;
        this.setExpandableX();
    }
    
    public GuiRowLabel set(CompiledText text) {
        this.text = text;
        return this;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        if (selected)
            text.setDefaultColor(rect.inside(mouseX, mouseY) ? ColorUtils.rgba(230, 230, 0, 255) : ColorUtils.rgba(200, 200, 0, 255));
        else if (rect.inside(mouseX, mouseY))
            text.setDefaultColor(ColorUtils.YELLOW);
        else
            text.setDefaultColor(ColorUtils.WHITE);
        super.renderContent(graphics, control, rect, mouseX, mouseY);
        text.setDefaultColor(ColorUtils.WHITE);
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        consumer.accept(button);
        playSound(SoundEvents.UI_BUTTON_CLICK);
        return true;
    }
    
}
