package team.creative.creativecore.common.util.mc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.client.CreativeCoreClient;

public class TickUtils {
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    private static float getFrameTimeClient() {
        return CreativeCoreClient.getFrameTime();
    }
    
    public static float getFrameTime(LevelAccessor level) {
        if (level.isClientSide())
            return getFrameTimeClient();
        return 1.0F;
    }
    
}
