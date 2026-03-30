package team.creative.creativecore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.Event;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;

public class CreativeFabricLoader implements ICreativeLoader {
    
    public final List<Runnable> RENDER_START = new ArrayList<>();
    public final List<Consumer> RENDER_GUI = new ArrayList<>();
    
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
    
    @Environment(EnvType.CLIENT)
    private void registerHudElement(Consumer run) {
        if (RENDER_GUI.isEmpty())
            HudElementRegistry.addLast(Identifier.tryBuild(CreativeCore.MODID, "gui"), new CreativeHudElement());
        RENDER_GUI.add(run);
    }
    
    @Override
    public void registerClientRenderGui(Consumer run) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            registerHudElement(run);
    }
    
    @Override
    public void registerClientRenderStart(Runnable run) {
        RENDER_START.add(run);
    }
    
    @Override
    public void registerReloadListener(Identifier identifier, PreparableReloadListener listener) {
        registerClientStarted(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            ReloadableResourceManager reloadableResourceManager = (ReloadableResourceManager) minecraft.getResourceManager();
            reloadableResourceManager.registerReloadListener(listener);
        });
    }
    
    @Override
    public void registerLevelTick(Consumer<ServerLevel> consumer) {
        ServerTickEvents.END_LEVEL_TICK.register(x -> consumer.accept(x));
    }
    
    @Override
    public void registerLevelTickStart(Consumer<ServerLevel> consumer) {
        ServerTickEvents.START_LEVEL_TICK.register(x -> consumer.accept(x));
    }
    
    @Override
    public void registerLoadLevel(Consumer<LevelAccessor> consumer) {
        ServerLevelEvents.LOAD.register((server, level) -> consumer.accept(level));
    }
    
    @Override
    public void registerUnloadLevel(Consumer<LevelAccessor> consumer) {
        ServerLevelEvents.UNLOAD.register((server, level) -> consumer.accept(level));
    }
    
    @Override
    public <T> void registerListener(Consumer<T> consumer) {}
    
    @Override
    public float getFluidViscosityMultiplier(Fluid fluid, Level level) {
        // 5.0F is the tick delay of Water
        return fluid.getTickDelay(level) / 5.0F;
    }
    
    @Override
    public float getFriction(LevelAccessor level, BlockPos pos, Entity entity) {
        return level.getBlockState(pos).getBlock().getFriction();
    }
    
    @Override
    public void registerClientStarted(Runnable run) {
        ClientLifecycleEvents.CLIENT_STARTED.register(x -> run.run());
    }
    
    @Override
    public void registerKeybind(Supplier<KeyMapping> supplier) {
        KeyMappingHelper.registerKeyMapping(supplier.get());
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
    
    @Environment(EnvType.CLIENT)
    private class CreativeHudElement implements HudElement {
        
        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
            for (Consumer consumer : RENDER_GUI) {
                consumer.accept(graphics);
            }
        }
    }
}
