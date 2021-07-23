package team.creative.creativecore.common.gui.dialog;

import java.util.function.BiConsumer;

import net.minecraft.nbt.CompoundTag;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.dialog.DialogGuiLayer.DialogButton;
import team.creative.creativecore.common.gui.handler.GuiLayerHandler;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.sync.LayerOpenPacket;

public class GuiDialogHandler extends GuiLayerHandler {
    
    @Override
    public GuiLayer create(IGuiIntegratedParent parent, CompoundTag nbt) {
        int[] array = nbt.getIntArray("buttons");
        DialogButton[] buttons = new DialogButton[array.length];
        for (int i = 0; i < array.length; i++)
            buttons[i] = DialogButton.values()[array[i]];
        return new DialogGuiLayer(nbt.getString("name"), null, buttons);
    }
    
    public static GuiLayer openDialog(IGuiParent parent, String name, BiConsumer<DialogGuiLayer, DialogButton> onClicked, DialogButton... buttons) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("name", name);
        int[] array = new int[buttons.length];
        for (int i = 0; i < array.length; i++)
            array[i] = buttons[i].ordinal();
        nbt.putIntArray("buttons", array);
        GuiLayer layer = parent.openLayer(new LayerOpenPacket("dialog", nbt));
        ((DialogGuiLayer) layer).onClicked = onClicked;
        return layer;
    }
    
    static {
        GuiLayerHandler.registerGuiLayerHandler("dialog", new GuiDialogHandler());
    }
    
}
