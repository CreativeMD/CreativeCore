package team.creative.creativecore.common.gui.control.simple;

import java.util.List;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.parser.LongValueParser;

public class GuiSeekBar extends GuiControl {
    
    public GuiSeekBar(IGuiParent parent, String name, LongSupplier posSupplier, LongSupplier maxSupplier) {
        this(parent, name, posSupplier, maxSupplier, LongValueParser.TIME_DURATION);
    }
    
    public GuiSeekBar(IGuiParent parent, String name, LongSupplier posSupplier, LongSupplier maxSupplier, LongValueParser parser) {
        super(parent, name);
        if (dist() != null)
            dist().init(posSupplier, maxSupplier, parser);
        this.tick();
    }
    
    @Override
    public GuiSeekBarDist dist() {
        return (GuiSeekBarDist) super.dist();
    }
    
    public GuiSeekBar setOnTimeUpdate(LongConsumer consumer) {
        if (dist() != null)
            dist().setOnTimeUpdate(consumer);
        return this;
    }
    
    public GuiSeekBar setOnLastTimeUpdate(LongConsumer consumer) {
        if (dist() != null)
            dist().setOnLastTimeUpdate(consumer);
        return this;
    }
    
    public void setPosition(long value) {
        if (dist() != null)
            dist().setPosition(value);
    }
    
    @Override
    public GuiSeekBar setTooltip(List<Component> tooltip) {
        super.setTooltip(tooltip);
        return this;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {
        if (dist() != null)
            dist().tick();
    }
    
    public static interface GuiSeekBarDist extends GuiControlDistHandler {
        
        public void init(LongSupplier posSupplier, LongSupplier maxSupplier, LongValueParser parser);
        
        public void setOnTimeUpdate(LongConsumer consumer);
        
        public void setOnLastTimeUpdate(LongConsumer consumer);
        
        public void setPosition(long value);
        
        public void tick();
    }
}