package team.creative.creativecore.common.gui.dialog;

import java.util.function.BiConsumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.dialog.DialogGuiLayer.DialogButton;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobalLayer;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;

public class GuiDialogHandler {
    
    public static final GuiSyncGlobalLayer<DialogGuiLayer> DIALOG_HANDLER = GuiSyncHolder.GLOBAL.layer("dialog", (nbt) -> {
        int[] array = nbt.getIntArray("buttons");
        DialogButton[] buttons = new DialogButton[array.length];
        for (int i = 0; i < array.length; i++)
            buttons[i] = DialogButton.values()[array[i]];
        return new DialogGuiLayer(nbt.getString("name"), Component.Serializer.fromJson(nbt.getString("title")), null, buttons);
    });
    
    public static void init() {}
    
    public static GuiLayer openDialog(IGuiIntegratedParent parent, String name, BiConsumer<DialogGuiLayer, DialogButton> onClicked, DialogButton... buttons) {
        return openDialog(parent, name, Component.translatable("dialog." + name), onClicked, buttons);
    }
    
    public static GuiLayer openDialog(IGuiIntegratedParent parent, String name, Component title, BiConsumer<DialogGuiLayer, DialogButton> onClicked, DialogButton... buttons) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("name", name);
        nbt.putString("title", Component.Serializer.toJson(title));
        int[] array = new int[buttons.length];
        for (int i = 0; i < array.length; i++)
            array[i] = buttons[i].ordinal();
        nbt.putIntArray("buttons", array);
        DialogGuiLayer layer = DIALOG_HANDLER.open(parent, nbt);
        layer.onClicked = onClicked;
        return layer;
    }
}
