package team.creative.creativecore;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import team.creative.creativecore.client.ClientLoader;

public class CreativeForgeLoader implements ICreativeLoader {
    
    @Override
    public void registerDisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest) {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(suppliedVersion, remoteVersionTest));
    }
    
    @Override
    public String ignoreServerNetworkConstant() {
        return NetworkConstants.IGNORESERVERONLY;
    }
    
    @Override
    public void registerClient(ClientLoader loader) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent x) -> loader.onInitializeClient());
        MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent x) -> loader.registerClientCommands(x.getDispatcher()));
    }
    
    @Override
    public void registerClientTick(Runnable run) {
        MinecraftForge.EVENT_BUS.addListener((ClientTickEvent x) -> {
            if (x.phase == Phase.START)
                run.run();
        });
    }
    
    @Override
    public void registerClientRender(Runnable run) {
        MinecraftForge.EVENT_BUS.addListener((RenderTickEvent x) -> {
            if (x.phase == Phase.END)
                run.run();
        });
    }
    
    @Override
    public void registerLoadLevel(Consumer<LevelAccessor> consumer) {
        MinecraftForge.EVENT_BUS.addListener((WorldEvent.Load x) -> consumer.accept(x.getWorld()));
    }
    
}
