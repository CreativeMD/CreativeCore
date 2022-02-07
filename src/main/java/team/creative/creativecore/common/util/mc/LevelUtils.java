package team.creative.creativecore.common.util.mc;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import team.creative.creativecore.common.level.CreativeLevel;

public class LevelUtils {
    
    public static boolean checkIfChunkExists(LevelChunk chunk) {
        if (chunk == null)
            return false;
        if (chunk instanceof EmptyLevelChunk)
            return false;
        return true;
    }
    
    public static void dropItem(Level level, List<ItemStack> stacks, BlockPos pos) {
        for (int i = 0; i < stacks.size(); i++)
            dropItem(level, stacks.get(i), pos);
    }
    
    public static void dropItem(Level level, ItemStack stack, BlockPos pos) {
        if (stack == null)
            return;
        if (level instanceof CreativeLevel) {
            pos = ((CreativeLevel) level).transformToRealWorld(pos);
            level = ((CreativeLevel) level).getRealLevel();
        }
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
    
    public static void dropItem(Player player, ItemStack stack) {
        if (stack != null)
            dropItem(player.level, stack, player.blockPosition());
    }
    
    public static void dropItem(Player player, List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); i++)
            dropItem(player, stacks.get(i));
    }
    
}
