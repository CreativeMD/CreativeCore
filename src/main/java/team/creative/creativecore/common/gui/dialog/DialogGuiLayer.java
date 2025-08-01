package team.creative.creativecore.common.gui.dialog;

import java.util.function.BiConsumer;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class DialogGuiLayer extends GuiLayer {
    
    public DialogButton[] buttons;
    public BiConsumer<DialogGuiLayer, DialogButton> onClicked;
    public Component title;
    
    public DialogGuiLayer(boolean client, String name, Component title, BiConsumer<DialogGuiLayer, DialogButton> onClicked, DialogButton... buttons) {
        super(client, name);
        this.title = title;
        this.buttons = buttons;
        this.onClicked = onClicked;
        setAlign(Align.CENTER);
        setVAlign(VAlign.CENTER);
        setFlow(GuiFlow.STACK_Y);
    }
    
    @Override
    public void create() {
        add(new GuiLabel(this, "text").setTitle(title));
        GuiParent hBox = new GuiParent(this, GuiFlow.STACK_X);
        for (DialogButton button : buttons)
            hBox.add(new GuiButton(this, button.name(), x -> closeDialog(button)).setTitle(Component.translatable("dialog.button." + button.name())));
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
