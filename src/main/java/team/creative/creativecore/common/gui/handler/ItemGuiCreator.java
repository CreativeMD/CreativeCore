package team.creative.creativecore.common.gui.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.GuiLayer;

public interface ItemGuiCreator {
    
    public GuiLayer create(CompoundTag nbt, Player player);
    
}
