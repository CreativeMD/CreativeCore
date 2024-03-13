package team.creative.creativecore.common.gui.controls.simple;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.ValueParser;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

import java.util.List;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public class GuiSeekBar extends GuiControl {
    private final LongSupplier posSupplier;
    private final LongSupplier maxSupplier;
    public final LongConsumer posUpdateConsumer;

    public final ValueParser parser;
    public boolean grabbedSlider;

    public GuiSeekBar(String name, LongConsumer posUpdateConsumer, LongSupplier posSupplier, LongSupplier maxSupplier, ValueParser parser) {
        super(name);
        this.posSupplier = posSupplier;
        this.maxSupplier = maxSupplier;
        this.posUpdateConsumer = posUpdateConsumer;
        this.parser = parser;
    }

    public void setValue(long value) {
        this.posUpdateConsumer.accept(value);

        if (getParent() != null)
            raiseEvent(new GuiControlChangedEvent(this));
    }

    @Override
    public GuiControl setTooltip(List<Component> tooltip) {
        return super.setTooltip(tooltip);
    }

    public double getPercent() {
        return posSupplier.getAsLong() / (double) maxSupplier.getAsLong();
    }

    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        final long max = maxSupplier.getAsLong();
        final long pos = posSupplier.getAsLong();

        final double percent = pos / (double) max;
        PoseStack pose = graphics.pose();
        renderProgress(pose, control, rect, percent);
        GuiRenderHelper.drawStringCentered(pose, parser.parse(pos, max), (float) rect.getWidth(), (float) rect.getHeight(), this.getStyle().fontColor.toInt(), true);
    }

    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderProgress(PoseStack pose, GuiChildControl control, Rect rect, double percent) {
        this.getStyle().clickable.render(pose, 0, 0, (rect.getWidth() * percent), rect.getHeight());
    }

    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        grabbedSlider = true;
        this.mouseMoved(rect, x, y);
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void mouseMoved(Rect rect, double x, double y) {
        if (grabbedSlider) {
            int width = (int) rect.getWidth() - getContentOffset() * 2;

            final long value;
            if (x < getContentOffset())
                value = 0;
            else if (x > getContentOffset() + width)
                value = maxSupplier.getAsLong();
            else {
                int mouseOffsetX = (int) (x - getContentOffset());
                value = (long) ((this.maxSupplier.getAsLong()) * ((float) mouseOffsetX / (float) width));
            }
            this.setValue(value);
        }
    }

    @Override
    public void init() {}

    @Override
    public void closed() {}

    @Override
    public void tick() {}

    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.PROGRESSBAR;
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