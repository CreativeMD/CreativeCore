package team.creative.creativecore;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.Event;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;

public class CreativeFabricLoader implements ICreativeLoader {
    
    @Override
    public void registerDisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest) {}
    
    @Override
    public String ignoreServerNetworkConstant() {
        return "";
    }
    
    @Override
    public void register(CommonLoader loader) {}
    
    @Override
    public void registerClient(ClientLoader loader) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ClientCommandRegistrationCallback.EVENT.register((x, y) -> loader.registerClientCommands(x));
    }
    
    @Override
    public void registerClientTick(Runnable run) {
        ClientTickEvents.END_CLIENT_TICK.register(x -> run.run());
    }
    
    @Override
    public void registerClientRenderGui(Consumer run) {
        HudRenderCallback.EVENT.register((graphics, partialTicks) -> run.accept(graphics));
    }
    
    @Override
    public void registerClientRenderStart(Runnable run) {
        WorldRenderEvents.START.register(x -> run.run());
    }
    
    @Override
    public void registerLevelTick(Consumer<ServerLevel> consumer) {
        ServerTickEvents.END_WORLD_TICK.register(x -> consumer.accept(x));
    }
    
    @Override
    public void registerLevelTickStart(Consumer<ServerLevel> consumer) {
        ServerTickEvents.START_WORLD_TICK.register(x -> consumer.accept(x));
    }
    
    @Override
    public void registerLoadLevel(Consumer<LevelAccessor> consumer) {
        ServerWorldEvents.LOAD.register((server, level) -> consumer.accept(level));
    }
    
    @Override
    public void registerUnloadLevel(Consumer<LevelAccessor> consumer) {
        ServerWorldEvents.UNLOAD.register((server, level) -> consumer.accept(level));
    }
    
    @Override
    public <T> void registerListener(Consumer<T> consumer) {}
    
    @Override
    public float getFluidViscosityMultiplier(Fluid fluid, Level level) {
        // 5.0F is the tick delay of Water
        return fluid.getTickDelay(level) / 5.0F;
    }
    
    @Override
    public void registerClientStarted(Runnable run) {
        ClientLifecycleEvents.CLIENT_STARTED.register(x -> run.run());
    }
    
    @Override
    public void registerKeybind(Supplier<KeyMapping> supplier) {
        KeyBindingHelper.registerKeyBinding(supplier.get());
    }
    
    @Override
    public void postForge(Event event) {}
    
    @Override
    public boolean isModLoaded(String modid) {
        return false;
    }
    
    @Override
    public Side getOverallSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER ? Side.SERVER : Side.CLIENT;
    }
    
    @Override
    public Side getEffectiveSide() {
        return Side.SERVER; // Not supported on fabric
    }
    
}
