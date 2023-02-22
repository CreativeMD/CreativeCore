package team.creative.creativecore;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.KeyMapping;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.Event;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;
import team.creative.creativecore.common.gui.dialog.GuiDialogHandler;

public interface ICreativeLoader {
    
    public default void loadCommon() {
        GuiDialogHandler.init();
    }
    
    public Side getOverallSide();
    
    public Side getEffectiveSide();
    
    public void registerDisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest);
    
    public String ignoreServerNetworkConstant();
    
    public void register(CommonLoader loader);
    
    public void registerClient(ClientLoader loader);
    
    public void registerClientTick(Runnable run);
    
    public void registerClientRenderStart(Runnable run);
    
    public void registerClientRender(Runnable run);
    
    public void registerClientStarted(Runnable run);
    
    public void registerKeybind(Supplier<KeyMapping> supplier);
    
    public void registerLevelTick(Consumer<ServerLevel> consumer);
    
    public void registerLevelTickStart(Consumer<ServerLevel> consumer);
    
    public void registerLoadLevel(Consumer<LevelAccessor> consumer);
    
    public void registerUnloadLevel(Consumer<LevelAccessor> consumer);
    
    public <T> void registerListener(Consumer<T> consumer);
    
    public float getFluidViscosityMultiplier(Fluid fluid, Level level);
    
    public void postForge(Event event);
    
    public boolean isModLoaded(String modid);
    
}
