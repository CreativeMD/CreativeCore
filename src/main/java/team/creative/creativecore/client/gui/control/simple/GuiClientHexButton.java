package team.creative.creativecore.client.gui.control.simple;

import java.util.function.Consumer;

import team.creative.creativecore.client.gui.extension.GuiExtensionCreator;
import team.creative.creativecore.client.gui.extension.GuiExtensionCreator.ExtensionDirection;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiColorPicker;
import team.creative.creativecore.common.gui.control.simple.GuiColorPicker.GuiHexButton;
import team.creative.creativecore.common.gui.control.simple.GuiColorPicker.GuiHexButtonDist;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.util.mc.ColorUtils;

public class GuiClientHexButton extends GuiClientButton<GuiHexButton> implements GuiHexButtonDist {
    
    protected GuiExtensionCreator<GuiClientHexButton, GuiParent> ex = new GuiExtensionCreator<>(this);
    protected GuiColorPicker picker;
    protected Consumer<Integer> consumer;
    private boolean updateCall;
    
    public GuiClientHexButton(GuiHexButton control) {
        super(control);
    }
    
    @Override
    public void setConsumer(GuiColorPicker picker, Consumer<Integer> consumer) {
        this.picker = picker;
        this.pressed = x -> ex.toggle(this::createBox, ExtensionDirection.RIGHT);
        this.consumer = consumer;
    }
    
    @Override
    public void colorChanged() {
        if (ex.hasExtension()) {
            updateCall = true;
            ex.get().get("hex", GuiTextfield.class).setText(picker.color.hex());
        }
    }
    
    protected GuiParent createBox(GuiExtensionCreator<GuiClientHexButton, GuiParent> creator) {
        GuiParent parent = new GuiParent(control.getParent());
        GuiTextfield text = new GuiTextfield(parent, "hex").setText(picker.color.hex()).setHexOnly().setMaxStringLength(6);
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
