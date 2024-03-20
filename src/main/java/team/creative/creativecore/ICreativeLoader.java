package team.creative.creativecore;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;

public interface ICreativeLoader {
    
    public void registerDisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest);
    
    public String ignoreServerNetworkConstant();
    
    public void register(CommonLoader loader);
    
    public void registerClient(ClientLoader loader);
    
    public void registerClientTick(Runnable run);
    
    public void registerClientRender(Runnable run);
    
    public void registerClientStarted(Runnable run);
    
    public void registerLoadLevel(Consumer<LevelAccessor> consumer);
    
    public <T> void registerListener(Consumer<T> consumer);
    
    public void postForge(Event event);
    
    public boolean isModLoaded(String modid);
    
}
