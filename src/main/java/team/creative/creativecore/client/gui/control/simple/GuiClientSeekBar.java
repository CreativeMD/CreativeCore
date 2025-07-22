package team.creative.creativecore.client.gui.control.simple;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.render.gui.CreativeGuiGraphics;
import team.creative.creativecore.common.gui.control.simple.GuiSeekBar;
import team.creative.creativecore.common.gui.control.simple.GuiSeekBar.GuiSeekBarDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.parser.LongValueParser;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientSeekBar<T extends GuiSeekBar> extends GuiClientControl<T> implements GuiSeekBarDist {
    
    private LongSupplier posSupplier;
    private LongSupplier maxSupplier;
    public LongConsumer timeUpdate;
    public LongConsumer lastTimeUpdate;
    
    private long pos;
    private long max;
    
    public LongValueParser parser;
    public boolean grabbedSlider;
    
    public GuiClientSeekBar(T control) {
        super(control);
    }
    
    @Override
    public void init(LongSupplier posSupplier, LongSupplier maxSupplier, LongValueParser parser) {
        this.posSupplier = posSupplier;
        this.maxSupplier = maxSupplier;
        this.parser = parser;
    }
    
    @Override
    public void setOnTimeUpdate(LongConsumer consumer) {
        this.timeUpdate = consumer;
    }
    
    @Override
    public void setOnLastTimeUpdate(LongConsumer consumer) {
        this.lastTimeUpdate = consumer;
    }
    
    @Override
    public void setPosition(long value) {
        if (this.pos >= this.max)
            value = this.max;
        this.timeUpdate.accept(value);
        this.pos = value;
        if (this.getParent() != null)
            this.raiseEvent(new GuiControlChangedEvent(control));
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        final double percent = this.max > 0 ? pos / (double) max : 0;
        this.renderProgress(graphics, percent);
        ((CreativeGuiGraphics) graphics).drawStringCentered(parser.parse(pos, max), rect.getContentWidth(), rect.getContentHeight(), this.getStyle().fontColor.toInt(), true);
    }
    
    protected void renderProgress(GuiGraphics graphics, double percent) {
        this.getStyle().clickable.render(graphics, 0, 0, (rect.getContentWidth() * Math.min(percent, 1.0d)), rect.getContentHeight());
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            grabbedSlider = this.max > 0; // validates maxTime is not a custom state
            this.mouseMoved(x, y);
            return true;
        }
        return false;
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        if (grabbedSlider) {
            int width = rect.getWidth() - getContentOffset() * 2;
            
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
    public void mouseReleased(double x, double y, int button) {
        if (this.grabbedSlider) {
            this.lastTimeUpdate.accept(pos);
            this.grabbedSlider = false;
        }
    }
    
    @Override
    public void tick() {
        if (!grabbedSlider) {
            this.pos = posSupplier.getAsLong();
            this.max = maxSupplier.getAsLong();
        }
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
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
