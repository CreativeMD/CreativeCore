package team.creative.creativecore.common.gui.dialog;

import java.util.function.BiConsumer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class DialogGuiLayer extends GuiLayer {
    
    public DialogButton[] buttons;
    public BiConsumer<DialogGuiLayer, DialogButton> onClicked;
    public Component title;
    
    public DialogGuiLayer(String name, Component title, BiConsumer<DialogGuiLayer, DialogButton> onClicked, DialogButton... buttons) {
        super(name);
        this.title = title;
        this.buttons = buttons;
        this.onClicked = onClicked;
        this.align = Align.CENTER;
        this.valign = VAlign.CENTER;
        this.flow = GuiFlow.STACK_Y;
    }
    
    @Override
    public void create() {
        add(new GuiLabel("text").setTitle(title));
        GuiParent hBox = new GuiParent(GuiFlow.STACK_X);
        for (DialogButton button : buttons)
            hBox.add(new GuiButton(button.name(), x -> closeDialog(button)).setTitle(new TranslatableComponent("dialog.button." + button.name())));
        add(hBox);
    }
    
    public void closeDialog(DialogButton button) {
        closeTopLayer();
        onClicked.accept(this, button);
    }
    
    public static enum DialogButton {
        
        OK,
        YES,
        NO,
        MAYBE,
        CANCEL,
        ABORT,
        CONFIRM;
        
    }
    
}
