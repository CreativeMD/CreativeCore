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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent.ClientTickEvent;
import net.neoforged.neoforge.event.TickEvent.LevelTickEvent;
import net.neoforged.neoforge.event.TickEvent.Phase;
import net.neoforged.neoforge.event.TickEvent.RenderTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
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
        return IExtensionPoint.DisplayTest.IGNORESERVERONLY;
    }
    
    @Override
    public void register(CommonLoader loader) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent x) -> loader.onInitialize());
    }
    
    @Override
    public void registerClient(ClientLoader loader) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent x) -> loader.onInitializeClient());
            NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent x) -> loader.registerClientCommands(x.getDispatcher()));
        });
    }
    
    @Override
    public void registerClientTick(Runnable run) {
        NeoForge.EVENT_BUS.addListener((ClientTickEvent x) -> {
            if (x.phase == Phase.START)
                run.run();
        });
    }
    
    @Override
    public void registerClientRenderGui(Consumer run) {
        NeoForge.EVENT_BUS.addListener((RenderGuiEvent.Post x) -> run.accept(x.getGuiGraphics()));
    }
    
    @Override
    public void registerClientRenderStart(Runnable run) {
        NeoForge.EVENT_BUS.addListener((RenderTickEvent x) -> {
            if (x.phase == Phase.START)
                run.run();
        });
    }
    
    @Override
    public void registerLevelTick(Consumer<ServerLevel> consumer) {
        NeoForge.EVENT_BUS.addListener((LevelTickEvent x) -> {
            if (x.phase == Phase.END && x.level instanceof ServerLevel level)
                consumer.accept(level);
        });
    }
    
    @Override
    public void registerLevelTickStart(Consumer<ServerLevel> consumer) {
        NeoForge.EVENT_BUS.addListener((LevelTickEvent x) -> {
            if (x.phase == Phase.START && x.level instanceof ServerLevel level)
                consumer.accept(level);
        });
        
    }
    
    @Override
    public void registerUnloadLevel(Consumer<LevelAccessor> consumer) {
        NeoForge.EVENT_BUS.addListener((LevelEvent.Unload x) -> consumer.accept(x.getLevel()));
    }
    
    @Override
    public void registerLoadLevel(Consumer<LevelAccessor> consumer) {
        NeoForge.EVENT_BUS.addListener((LevelEvent.Load x) -> consumer.accept(x.getLevel()));
    }
    
    @Override
    public void registerListener(Consumer consumer) {
        NeoForge.EVENT_BUS.addListener(consumer);
    }
    
    @Override
    public void registerClientStarted(Runnable run) {
        run.run();
    }
    
    @Override
    public void postForge(Event event) {
        NeoForge.EVENT_BUS.post(event);
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
