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
        GuiRow row = new GuiRow(parent);
        add(row);
        
        GuiColumn sliders = new GuiColumn(parent, GuiFlow.STACK_Y);
        sliders.setSpacing(-1);
        row.addColumn(sliders);
        
        GuiParent red = new GuiParent(parent, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(red);
        red.add(new GuiButtonHold(parent, "r-", x -> {
            ((GuiColoredSteppedSlider) get("r")).stepDown();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        red.add(new GuiColoredSteppedSlider(parent, "r", this, ColorPart.RED).setExpandableX());
        red.add(new GuiButtonHold(parent, "r+", x -> {
            ((GuiColoredSteppedSlider) get("r")).stepUp();
            onColorChanged();
        }).setTitle(Component.literal(">")));
        
        GuiParent green = new GuiParent(parent, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(green);
        green.add(new GuiButtonHold(parent, "g-", x -> {
            ((GuiColoredSteppedSlider) get("g")).stepDown();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        green.add(new GuiColoredSteppedSlider(parent, "g", this, ColorPart.GREEN).setExpandableX());
        green.add(new GuiButtonHold(parent, "g+", x -> {
            ((GuiColoredSteppedSlider) get("g")).stepUp();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        
        GuiParent blue = new GuiParent(parent, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(blue);
        blue.add(new GuiButtonHold(parent, "b-", x -> {
            ((GuiColoredSteppedSlider) get("b")).stepDown();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
        blue.add(new GuiColoredSteppedSlider(parent, "b", this, ColorPart.BLUE).setExpandableX());
        blue.add(new GuiButtonHold(parent, "b+", x -> {
            ((GuiColoredSteppedSlider) get("b")).stepUp();
            onColorChanged();
        }).setHoverEffect(true).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        
        if (hasAlpha) {
            GuiParent alpha = new GuiParent(parent, GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
            sliders.add(alpha);
            alpha.add(new GuiButtonHold(parent, "a-", x -> {
                ((GuiColoredSteppedSlider) get("a")).stepDown();
                onColorChanged();
            }).setHoverEffect(true).setTitle(Component.literal("<")).setFormatting(ControlFormatting.TRANSPARENT));
            alpha.add(new GuiColoredSteppedSlider(parent, "a", this, ColorPart.ALPHA).setExpandableX());
            alpha.add(new GuiButtonHold(parent, "a+", x -> {
                ((GuiColoredSteppedSlider) get("a")).stepUp();
                onColorChanged();
            }).setHoverEffect(true).setTitle(Component.literal(">")).setFormatting(ControlFormatting.TRANSPARENT));
        } else
            color.setAlpha(255);
        
        GuiColumn plate = new GuiColumn(parent, 30, GuiFlow.STACK_Y);
        plate.setAlign(Align.CENTER).setVAlign(VAlign.CENTER);
        row.addColumn(plate);
        
        plate.add(new GuiColorPlate(parent, "plate", color).setDim(20, 20));
        
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