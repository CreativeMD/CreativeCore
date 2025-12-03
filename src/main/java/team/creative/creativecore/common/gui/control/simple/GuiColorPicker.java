package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.parent.GuiColumn;
import team.creative.creativecore.common.gui.control.parent.GuiRow;
import team.creative.creativecore.common.gui.control.simple.GuiButton.GuiButtonDist;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.mc.ColorUtils;
import team.creative.creativecore.common.util.mc.ColorUtils.ColorPart;
import team.creative.creativecore.common.util.type.Color;

public class GuiColorPicker extends GuiParent {
    
    protected GuiColoredSteppedSlider r;
    protected GuiColoredSteppedSlider g;
    protected GuiColoredSteppedSlider b;
    protected GuiColoredSteppedSlider a;
    protected GuiHexButton hex;
    public Color color;
    
    public GuiColorPicker(IGuiParent parent, String name, Color color, boolean hasAlpha, int alphaMin) {
        super(parent, name);
        this.color = new Color(color);
        GuiRow row = new GuiRow(this);
        add(row);
        
        GuiColumn sliders = new GuiColumn(this, GuiFlow.STACK_Y);
        sliders.setSpacing(-1);
        row.addColumn(sliders);
        
        GuiParent red = new GuiParent(this, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(red);
        red.add(new GuiButtonHold(red, "r-", x -> r.stepDown()).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        red.add(r = (GuiColoredSteppedSlider) new GuiColoredSteppedSlider(red, "r", this, ColorPart.RED).setExpandableX());
        red.add(new GuiButtonHold(red, "r+", x -> r.stepUp()).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        
        GuiParent green = new GuiParent(this, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(green);
        green.add(new GuiButtonHold(green, "g-", x -> g.stepDown()).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        green.add(g = (GuiColoredSteppedSlider) new GuiColoredSteppedSlider(green, "g", this, ColorPart.GREEN).setExpandableX());
        green.add(new GuiButtonHold(green, "g+", x -> g.stepUp()).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        
        GuiParent blue = new GuiParent(this, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(blue);
        blue.add(new GuiButtonHold(blue, "b-", x -> b.stepDown()).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        blue.add(b = (GuiColoredSteppedSlider) new GuiColoredSteppedSlider(blue, "b", this, ColorPart.BLUE).setExpandableX());
        blue.add(new GuiButtonHold(blue, "b+", x -> b.stepUp()).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        
        if (hasAlpha) {
            GuiParent alpha = new GuiParent(this, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
            sliders.add(alpha);
            alpha.add(new GuiButtonHold(alpha, "a-", x -> a.stepDown()).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
            alpha.add(a = (GuiColoredSteppedSlider) new GuiColoredSteppedSlider(alpha, "a", this, ColorPart.ALPHA).setExpandableX());
            alpha.add(new GuiButtonHold(alpha, "a+", x -> a.stepUp()).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        } else
            this.color.setAlpha(255);
        
        GuiColumn plate = new GuiColumn(this, 30, GuiFlow.STACK_Y);
        plate.setAlign(Align.CENTER).setVAlign(VAlign.CENTER);
        row.addColumn(plate);
        
        plate.add(new GuiColorPlate(plate, "plate", this.color).setDim(20, 20));
        plate.add(hex = new GuiHexButton(plate, this, name, hex -> {
            if (hex != this.color.toInt()) {
                r.setValueSilent(ColorUtils.red(hex));
                g.setValueSilent(ColorUtils.green(hex));
                b.setValueSilent(ColorUtils.blue(hex));
                onColorChanged(false);
            }
        }));
    }
    
    public void setColor(Color color) {
        r.setValueSilent(color.getRed());
        g.setValueSilent(color.getGreen());
        b.setValueSilent(color.getBlue());
        if (a != null)
            a.setValueSilent(color.getAlpha());
        onColorChanged(true);
    }
    
    public void onColorChanged(boolean notifyHex) {
        if (notifyHex)
            hex.colorChanged();
        raiseEvent(new GuiControlChangedEvent<>(this));
    }
    
    public static class GuiHexButton extends GuiButton {
        
        public GuiHexButton(IGuiParent parent, GuiColorPicker picker, String name, Consumer<Integer> consumer) {
            super(parent, name, null);
            if (dist() != null)
                dist().setConsumer(picker, consumer);
            setTitle(Component.literal("#"));
        }
        
        @Override
        public GuiHexButtonDist dist() {
            return (GuiHexButtonDist) super.dist();
        }
        
        public void colorChanged() {
            if (dist() != null)
                dist().colorChanged();
        }
        
    }
    
    public static interface GuiHexButtonDist extends GuiButtonDist {
        
        public void setConsumer(GuiColorPicker picker, Consumer<Integer> consumer);
        
        public void colorChanged();
        
    }
}