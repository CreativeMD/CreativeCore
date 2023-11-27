package team.creative.creativecore.common.gui.creator;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import team.creative.creativecore.common.gui.GuiLayer;

public interface BlockGuiCreator {
    
    public GuiLayer create(CompoundTag nbt, Level level, BlockPos pos, BlockState state, Player player);
    
}
