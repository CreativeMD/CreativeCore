package team.creative.creativecore;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkConstants;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;

public class CreativeForgeLoader implements ICreativeLoader {
    
    @Override
    public Side getOverallSide() {
        return FMLEnvironment.dist.isClient() ? Side.CLIENT : Side.SERVER;
    }
    
    @Override
    public void registerDisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest) {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(suppliedVersion, remoteVersionTest));
    }
    
    @Override
    public String ignoreServerNetworkConstant() {
        return NetworkConstants.IGNORESERVERONLY;
    }
    
    @Override
    public void register(CommonLoader loader) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent x) -> loader.onInitialize());
    }
    
    @Override
    public void registerClient(ClientLoader loader) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent x) -> loader.onInitializeClient());
            MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent x) -> loader.registerClientCommands(x.getDispatcher()));
        });
    }
    
    @Override
    public void registerClientTick(Runnable run) {
        MinecraftForge.EVENT_BUS.addListener((ClientTickEvent x) -> {
            if (x.phase == Phase.START)
                run.run();
        });
    }
    
    @Override
    public void registerClientRenderGui(Consumer run) {
        MinecraftForge.EVENT_BUS.addListener((RenderGuiEvent.Post x) -> run.accept(x.getPoseStack()));
    }
    
    @Override
    public void registerClientRenderStart(Runnable run) {
        MinecraftForge.EVENT_BUS.addListener((RenderTickEvent x) -> {
            if (x.phase == Phase.START)
                run.run();
        });
    }
    
    @Override
    public void registerLevelTick(Consumer<ServerLevel> consumer) {
        MinecraftForge.EVENT_BUS.addListener((LevelTickEvent x) -> {
            if (x.phase == Phase.END && x.level instanceof ServerLevel level)
                consumer.accept(level);
        });
    }
    
    @Override
    public void registerLevelTickStart(Consumer<ServerLevel> consumer) {
        MinecraftForge.EVENT_BUS.addListener((LevelTickEvent x) -> {
            if (x.phase == Phase.START && x.level instanceof ServerLevel level)
                consumer.accept(level);
        });
        
    }
    
    @Override
    public void registerUnloadLevel(Consumer<LevelAccessor> consumer) {
        MinecraftForge.EVENT_BUS.addListener((LevelEvent.Unload x) -> consumer.accept(x.getLevel()));
    }
    
    @Override
    public void registerLoadLevel(Consumer<LevelAccessor> consumer) {
        MinecraftForge.EVENT_BUS.addListener((LevelEvent.Load x) -> consumer.accept(x.getLevel()));
    }
    
    @Override
    public void registerListener(Consumer consumer) {
        MinecraftForge.EVENT_BUS.addListener(consumer);
    }
    
    @Override
    public void registerClientStarted(Runnable run) {
        run.run();
    }
    
    @Override
    public void postForge(Event event) {
        MinecraftForge.EVENT_BUS.post(event);
    }
    
    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
    
    @Override
    public float getFluidViscosityMultiplier(Fluid fluid, Level level) {
        return fluid.getFluidType().getViscosity() / 1000f;
    }
    
    @Override
    public void registerKeybind(Supplier<KeyMapping> supplier) {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, supplier.get());
    }
    
    @Override
    public Side getEffectiveSide() {
        return EffectiveSide.get().isClient() ? Side.CLIENT : Side.SERVER;
    }
    
}
