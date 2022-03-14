package team.creative.creativecore;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.level.LevelAccessor;
import team.creative.creativecore.client.ClientLoader;

public interface ICreativeLoader {
    
    public void registerDisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest);
    
    public String ignoreServerNetworkConstant();
    
    public void registerClient(ClientLoader loader);
    
    public void registerClientTick(Runnable run);
    
    public void registerClientRender(Runnable run);
    
    public void registerLoadLevel(Consumer<LevelAccessor> consumer);
    
}
