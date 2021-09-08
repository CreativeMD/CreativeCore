package team.creative.creativecore.common.config.gui;

import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.premade.SubContainerEmpty;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.utils.player.PlayerSelector;

import net.minecraft.nbt.NBTTagCompound;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public class GuiPlayerSelectorButton extends GuiButton {
    
    private PlayerSelector info;
    
    public GuiPlayerSelectorButton(String name, int x, int y, int width, int height, PlayerSelector info) {
        super(name, getLabelText(info), x, y, width, height);
        this.info = info;
    }
    
    public void set(PlayerSelector info) {
        this.info = info;
        this.caption = getLabelText(info);
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public static String getLabelText(PlayerSelector value) {
        String info = value.info();
        if (info.length() > 25)
            info = info.substring(22) + "...";
        return info;
    }
    
    @Override
    public void onClicked(int x, int y, int button) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("dialog", true);
        SubGui gui = new SubGuiPlayerSelectorDialog(this);
        gui.container = new SubContainerEmpty(getPlayer());
        gui.gui = getGui().gui;
        getGui().gui.addLayer(gui);
        PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, gui.gui.getLayers().size() - 1, false));
        gui.onOpened();
    }
    
    public PlayerSelector get() {
        return info;
    }
    
}
