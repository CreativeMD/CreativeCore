package team.creative.creativecore.common.level;

import java.util.function.Supplier;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.WritableLevelData;

public class FakeServerWorld extends FakeLevel {
    
    private final MinecraftServer server;
    
    protected FakeServerWorld(MinecraftServer server, WritableLevelData worldInfo, int radius, Supplier<ProfilerFiller> supplier, boolean client, boolean debug, long seed) {
        super(worldInfo, radius, supplier, client, debug, seed);
        this.server = server;
    }
    
    @Override
    public MinecraftServer getServer() {
        return server;
    }
    
}
