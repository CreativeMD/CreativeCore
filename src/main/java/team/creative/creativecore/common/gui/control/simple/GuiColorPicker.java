package team.creative.creativecore.common.gui.control.simple;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.parent.GuiColumn;
import team.creative.creativecore.common.gui.control.parent.GuiRow;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.mc.ColorUtils.ColorPart;
import team.creative.creativecore.common.util.type.Color;

public class GuiColorPicker extends GuiParent {
    
    public Color color;
    
    public GuiColorPicker(IGuiParent parent, String name, Color color, boolean hasAlpha, int alphaMin) {
        super(parent, name);
        this.color = color;
        GuiRow row = new GuiRow(this);
        add(row);
        
        GuiColumn sliders = new GuiColumn(this, GuiFlow.STACK_Y);
        sliders.setSpacing(-1);
        row.addColumn(sliders);
        
        GuiParent red = new GuiParent(this, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(red);
        red.add(new GuiButtonHold(this, "r-", x -> {
            ((GuiColoredSteppedSlider) get("r")).stepDown();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        red.add(new GuiColoredSteppedSlider(this, "r", this, ColorPart.RED).setExpandableX());
        red.add(new GuiButtonHold(this, "r+", x -> {
            ((GuiColoredSteppedSlider) get("r")).stepUp();
            onColorChanged();
        }).setTitle(Component.literal(">")));
        
        GuiParent green = new GuiParent(this, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(green);
        green.add(new GuiButtonHold(this, "g-", x -> {
            ((GuiColoredSteppedSlider) get("g")).stepDown();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        green.add(new GuiColoredSteppedSlider(this, "g", this, ColorPart.GREEN).setExpandableX());
        green.add(new GuiButtonHold(this, "g+", x -> {
            ((GuiColoredSteppedSlider) get("g")).stepUp();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        
        GuiParent blue = new GuiParent(this, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(blue);
        blue.add(new GuiButtonHold(this, "b-", x -> {
            ((GuiColoredSteppedSlider) get("b")).stepDown();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        blue.add(new GuiColoredSteppedSlider(this, "b", this, ColorPart.BLUE).setExpandableX());
        blue.add(new GuiButtonHold(this, "b+", x -> {
            ((GuiColoredSteppedSlider) get("b")).stepUp();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        
        if (hasAlpha) {
            GuiParent alpha = new GuiParent(this, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
            sliders.add(alpha);
            alpha.add(new GuiButtonHold(this, "a-", x -> {
                ((GuiColoredSteppedSlider) get("a")).stepDown();
                onColorChanged();
            }).setHoverEffect(true).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
            alpha.add(new GuiColoredSteppedSlider(this, "a", this, ColorPart.ALPHA).setExpandableX());
            alpha.add(new GuiButtonHold(this, "a+", x -> {
                ((GuiColoredSteppedSlider) get("a")).stepUp();
                onColorChanged();
            }).setHoverEffect(true).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        } else
            color.setAlpha(255);
        
        GuiColumn plate = new GuiColumn(this, 30, GuiFlow.STACK_Y);
        plate.setAlign(Align.CENTER).setVAlign(VAlign.CENTER);
        row.addColumn(plate);
        
        plate.add(new GuiColorPlate(this, "plate", color).setDim(20, 20));
        
        registerEventChanged(x -> {
            if (x.control.is("r", "g", "b", "a"))
                onColorChanged();
        });
    }
    
    public void setColor(Color color) {
        this.color.set(color);
        get("r", GuiColoredSteppedSlider.class).setValue(color.getRed());
        get("g", GuiColoredSteppedSlider.class).setValue(color.getGreen());
        get("b", GuiColoredSteppedSlider.class).setValue(color.getBlue());
        if (has("a"))
            get("a", GuiColoredSteppedSlider.class).setValue(color.getAlpha());
    }
    
    public void onColorChanged() {
        raiseEvent(new GuiControlChangedEvent<>(this));
    }
    
}