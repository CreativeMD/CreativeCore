package team.creative.creativecore.common.gui.creator;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.gui.GuiLayer;

public interface ItemGuiCreator {
    
    public GuiLayer create(CompoundTag nbt, Player player);
    
}
