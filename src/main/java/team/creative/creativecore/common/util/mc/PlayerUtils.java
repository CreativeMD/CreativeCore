package team.creative.creativecore.common.util.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerUtils {
    
    @OnlyIn(Dist.CLIENT)
    private static GameType getGameTypeClient(Player player) {
        return Minecraft.getInstance().gameMode.getPlayerMode();
    }
    
    public static GameType getGameType(Player player) {
        if (player instanceof ServerPlayer)
            return ((ServerPlayer) player).gameMode.getGameModeForPlayer();
        return getGameTypeClient(player);
    }
    
    public static double getReach(Player player) {
        double attrib = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
        return player.isCreative() ? attrib : attrib - 0.5;
    }
}
