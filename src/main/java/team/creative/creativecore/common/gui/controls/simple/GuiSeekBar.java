package team.creative.creativecore.common.gui.controls.simple;

import java.util.List;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.GuiRenderHelper;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.parser.LongValueParser;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiSeekBar extends GuiControl {
    private final LongSupplier posSupplier;
    private final LongSupplier maxSupplier;
    public LongConsumer timeUpdate;
    public LongConsumer lastTimeUpdate;
    
    private long pos;
    private long max;
    
    public final LongValueParser parser;
    public boolean grabbedSlider;
    
    public GuiSeekBar(String name, LongSupplier posSupplier, LongSupplier maxSupplier) {
        this(name, posSupplier, maxSupplier, LongValueParser.TIME_DURATION);
    }
    
    public GuiSeekBar(String name, LongSupplier posSupplier, LongSupplier maxSupplier, LongValueParser parser) {
        super(name);
        this.posSupplier = posSupplier;
        this.maxSupplier = maxSupplier;
        this.parser = parser;
        this.tick();
    }
    
    public GuiSeekBar setOnTimeUpdate(LongConsumer consumer) {
        this.timeUpdate = consumer;
        return this;
    }
    
    public GuiSeekBar setOnLastTimeUpdate(LongConsumer consumer) {
        this.lastTimeUpdate = consumer;
        return this;
    }
    
    public void setPosition(long value) {
        if (this.pos >= this.max) value = this.max;
        this.timeUpdate.accept(value);
        this.pos = value;
        if (this.getParent() != null)
            this.raiseEvent(new GuiControlChangedEvent(this));
    }
    
    @Override
    public GuiSeekBar setTooltip(List<Component> tooltip) {
        super.setTooltip(tooltip);
        return this;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack pose, GuiChildControl control, Rect rect, int mouseX, int mouseY) {
        final double percent = this.max > 0 ? pos / (double) max : 0;
        this.renderProgress(pose, control, rect, percent);
        GuiRenderHelper.drawStringCentered(pose, parser.parse(pos, max), (float) rect.getWidth(), (float) rect.getHeight(), this.getStyle().fontColor.toInt(), true);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderProgress(PoseStack pose, GuiChildControl control, Rect rect, double percent) {
        this.getStyle().clickable.render(pose, 0, 0, (rect.getWidth() * Math.min(percent, 1.0d)), rect.getHeight());
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (button == 0) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            grabbedSlider = this.max > 0; // validates maxTime is not a custom state
            this.mouseMoved(rect, x, y);
            return true;
        }
        return false;
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
            this.setPosition(value);
        }
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void mouseReleased(Rect rect, double x, double y, int button) {
        if (this.grabbedSlider) {
            this.lastTimeUpdate.accept(pos);
            this.grabbedSlider = false;
        }
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {
        if (!grabbedSlider) {
            this.pos = posSupplier.getAsLong();
            this.max = maxSupplier.getAsLong();
        }
    }
    
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
        return 14;
    }
}