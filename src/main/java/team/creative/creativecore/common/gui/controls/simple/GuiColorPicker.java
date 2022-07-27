package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn;
import team.creative.creativecore.common.gui.controls.parent.GuiRow;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.mc.ColorUtils.ColorPart;
import team.creative.creativecore.common.util.type.Color;

public class GuiColorPicker extends GuiParent {
    
    public Color color;
    
    public GuiColorPicker(String name, Color color, boolean hasAlpha, int alphaMin) {
        super(name);
        this.color = color;
        GuiRow row = new GuiRow();
        add(row);
        
        GuiColumn sliders = new GuiColumn(GuiFlow.STACK_Y);
        row.addColumn(sliders);
        
        GuiParent red = new GuiParent(GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(red);
        red.add(new GuiButtonHoldSlim("r-", x -> {
            ((GuiColoredSteppedSlider) get("r")).stepDown();
            onColorChanged();
        }).setTitle(Component.literal("<")));
        red.add(new GuiColoredSteppedSlider("r", this, ColorPart.RED).setExpandableX());
        red.add(new GuiButtonHoldSlim("r+", x -> {
            ((GuiColoredSteppedSlider) get("r")).stepUp();
            onColorChanged();
        }).setTitle(Component.literal(">")));
        
        GuiParent green = new GuiParent(GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(green);
        green.add(new GuiButtonHoldSlim("g-", x -> {
            ((GuiColoredSteppedSlider) get("g")).stepDown();
            onColorChanged();
        }).setTitle(Component.literal("<")));
        green.add(new GuiColoredSteppedSlider("g", this, ColorPart.GREEN).setExpandableX());
        green.add(new GuiButtonHoldSlim("g+", x -> {
            ((GuiColoredSteppedSlider) get("g")).stepUp();
            onColorChanged();
        }).setTitle(Component.literal(">")));
        
        GuiParent blue = new GuiParent(GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(blue);
        blue.add(new GuiButtonHoldSlim("b-", x -> {
            ((GuiColoredSteppedSlider) get("b")).stepDown();
            onColorChanged();
        }).setTitle(Component.literal("<")));
        blue.add(new GuiColoredSteppedSlider("b", this, ColorPart.BLUE).setExpandableX());
        blue.add(new GuiButtonHoldSlim("b+", x -> {
            ((GuiColoredSteppedSlider) get("b")).stepUp();
            onColorChanged();
        }).setTitle(Component.literal(">")));
        
        if (hasAlpha) {
            GuiParent alpha = new GuiParent(GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
            sliders.add(alpha);
            alpha.add(new GuiButtonHoldSlim("a-", x -> {
                ((GuiColoredSteppedSlider) get("a")).stepDown();
                onColorChanged();
            }).setTitle(Component.literal("<")));
            alpha.add(new GuiColoredSteppedSlider("a", this, ColorPart.ALPHA).setExpandableX());
            alpha.add(new GuiButtonHoldSlim("a+", x -> {
                ((GuiColoredSteppedSlider) get("a")).stepUp();
                onColorChanged();
            }).setTitle(Component.literal(">")));
        } else
            color.setAlpha(255);
        
        GuiColumn plate = new GuiColumn(30, GuiFlow.STACK_Y);
        plate.align = Align.CENTER;
        plate.valign = VAlign.CENTER;
        row.addColumn(plate);
        
        plate.add(new GuiColorPlate("plate", 20, 20, color));
        
        registerEventChanged(x -> {
            if (x.control.is("r", "g", "b", "a"))
                onColorChanged();
        });
    }
    
    public void setColor(Color color) {
        this.color.set(color);
        ((GuiColoredSteppedSlider) get("r")).value = color.getRed();
        ((GuiColoredSteppedSlider) get("g")).value = color.getGreen();
        ((GuiColoredSteppedSlider) get("b")).value = color.getBlue();
        if (has("a"))
            ((GuiColoredSteppedSlider) get("a")).value = color.getAlpha();
    }
    
    public void onColorChanged() {
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
}