package team.creative.creativecore.common.world;

import java.util.function.Supplier;

import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISpawnWorldInfo;

public class FakeWorldServer extends FakeWorld {
    
    private final MinecraftServer server;
    
    protected FakeWorldServer(MinecraftServer server, ISpawnWorldInfo worldInfo, int radius, Supplier<IProfiler> supplier, boolean client, boolean debug, long seed) {
        super(worldInfo, radius, supplier, client, debug, seed);
        this.server = server;
    }
    
    @Override
    public MinecraftServer getServer() {
        return server;
    }
    
}
