package team.creative.creativecore.common.gui.control.simple;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.parent.GuiColumn;
import team.creative.creativecore.common.gui.control.parent.GuiRow;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.common.gui.extension.GuiExtensionCreator.ExtensionDirection;
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
    
    public GuiColorPicker(String name, Color color, boolean hasAlpha, int alphaMin) {
        super(name);
        this.color = new Color(color);
        GuiRow row = new GuiRow();
        add(row);
        
        GuiColumn sliders = new GuiColumn(GuiFlow.STACK_Y);
        sliders.spacing = -1;
        row.addColumn(sliders);
        
        GuiParent red = new GuiParent(GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(red);
        red.add(new GuiButtonHoldSlim("r-", x -> r.stepDown()).setTitle(Component.literal("<")));
        red.add(r = (GuiColoredSteppedSlider) new GuiColoredSteppedSlider("r", this, ColorPart.RED).setExpandableX());
        red.add(new GuiButtonHoldSlim("r+", x -> r.stepUp()).setTitle(Component.literal(">")));
        
        GuiParent green = new GuiParent(GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(green);
        green.add(new GuiButtonHoldSlim("g-", x -> g.stepDown()).setTitle(Component.literal("<")));
        green.add(g = (GuiColoredSteppedSlider) new GuiColoredSteppedSlider("g", this, ColorPart.GREEN).setExpandableX());
        green.add(new GuiButtonHoldSlim("g+", x -> g.stepUp()).setTitle(Component.literal(">")));
        
        GuiParent blue = new GuiParent(GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
        sliders.add(blue);
        blue.add(new GuiButtonHoldSlim("b-", x -> b.stepDown()).setTitle(Component.literal("<")));
        blue.add(b = (GuiColoredSteppedSlider) new GuiColoredSteppedSlider("b", this, ColorPart.BLUE).setExpandableX());
        blue.add(new GuiButtonHoldSlim("b+", x -> b.stepUp()).setTitle(Component.literal(">")));
        
        if (hasAlpha) {
            GuiParent alpha = new GuiParent(GuiFlow.STACK_X).setVAlign(VAlign.CENTER);
            sliders.add(alpha);
            alpha.add(new GuiButtonHoldSlim("a-", x -> a.stepDown()).setTitle(Component.literal("<")));
            alpha.add(a = (GuiColoredSteppedSlider) new GuiColoredSteppedSlider("a", this, ColorPart.ALPHA).setExpandableX());
            alpha.add(new GuiButtonHoldSlim("a+", x -> a.stepUp()).setTitle(Component.literal(">")));
        } else
            this.color.setAlpha(255);
        
        GuiColumn plate = new GuiColumn(30, GuiFlow.STACK_Y);
        plate.align = Align.CENTER;
        plate.valign = VAlign.CENTER;
        row.addColumn(plate);
        
        plate.add(new GuiColorPlate("plate", this.color).setDim(20, 20));
        plate.add(hex = new GuiHexButton(name, hex -> {
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
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    public class GuiHexButton extends GuiButton {
        
        protected GuiExtensionCreator<GuiHexButton, GuiParent> ex = new GuiExtensionCreator<>(this);
        protected Consumer<Integer> consumer;
        private boolean updateCall;
        
        public GuiHexButton(String name, Consumer<Integer> consumer) {
            super(name, null);
            this.pressed = x -> ex.toggle(this::createBox, ExtensionDirection.RIGHT);
            this.consumer = consumer;
            setTitle(Component.literal("#"));
        }
        
        public void colorChanged() {
            if (ex.hasExtension()) {
                updateCall = true;
                ex.get().get("hex", GuiTextfield.class).setText(color.hex());
            }
        }
        
        protected GuiParent createBox(GuiExtensionCreator<GuiButtonContext, GuiParent> creator) {
            GuiParent parent = new GuiParent();
            GuiTextfield text = new GuiTextfield("hex").setText(color.hex()).setHexOnly().setMaxStringLength(6);
            parent.add(text.setDim(42, 8));
            parent.registerEventChanged(x -> {
                if (updateCall)
                    updateCall = false;
                else
                    consumer.accept(ColorUtils.fromHex(text.getText()));
            });
            return parent;
        }
    }
}