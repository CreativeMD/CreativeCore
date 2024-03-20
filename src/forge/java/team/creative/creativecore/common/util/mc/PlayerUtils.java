package team.creative.creativecore.common.util.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

public class PlayerUtils {
    
    public static CompoundTag getPersistentData(Player player) {
        return player.getPersistentData();
    }
    
    @OnlyIn(Dist.CLIENT)
    private static GameType getGameTypeClient(Player player) {
        return Minecraft.getInstance().gameMode.getPlayerMode();
    }
    
    public static boolean isAdventure(Player player) {
        return getGameType(player) == GameType.ADVENTURE;
    }
    
    public static GameType getGameType(Player player) {
        if (player instanceof ServerPlayer)
            return ((ServerPlayer) player).gameMode.getGameModeForPlayer();
        return getGameTypeClient(player);
    }
    
    public static double getReach(Player player) {
        double attrib = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
        return player.isCreative() ? attrib : attrib - 0.5;
    }
    
    public static void addOrDrop(Player player, ItemStack stack) {
        if (!stack.isEmpty() && !player.addItem(stack))
            player.drop(stack, true, false);
    }
    
    public static void addOrDrop(Player player, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++)
            addOrDrop(player, container.getItem(i));
    }
}
