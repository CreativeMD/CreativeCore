package team.creative.creativecore.common.gui.dialog;

import java.util.function.BiConsumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.dialog.DialogGuiLayer.DialogButton;
import team.creative.creativecore.common.gui.handler.GuiLayerHandler;
import team.creative.creativecore.common.gui.packet.LayerOpenPacket;

public class GuiDialogHandler {
    
    public static final GuiLayerHandler DIALOG_HANDLER = (parent, nbt) -> {
        int[] array = nbt.getIntArray("buttons");
        DialogButton[] buttons = new DialogButton[array.length];
        for (int i = 0; i < array.length; i++)
            buttons[i] = DialogButton.values()[array[i]];
        return new DialogGuiLayer(nbt.getString("name"), Component.Serializer.fromJson(nbt.getString("title")), null, buttons);
    };
    
    public static GuiLayer openDialog(IGuiParent parent, String name, BiConsumer<DialogGuiLayer, DialogButton> onClicked, DialogButton... buttons) {
        return openDialog(parent, name, new TranslatableComponent("dialog." + name), onClicked, buttons);
    }
    
    public static GuiLayer openDialog(IGuiParent parent, String name, TranslatableComponent title, BiConsumer<DialogGuiLayer, DialogButton> onClicked, DialogButton... buttons) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("name", name);
        nbt.putString("title", Component.Serializer.toJson(title));
        int[] array = new int[buttons.length];
        for (int i = 0; i < array.length; i++)
            array[i] = buttons[i].ordinal();
        nbt.putIntArray("buttons", array);
        GuiLayer layer = parent.openLayer(new LayerOpenPacket(DIALOG_HANDLER, nbt));
        ((DialogGuiLayer) layer).onClicked = onClicked;
        return layer;
    }
    
}
