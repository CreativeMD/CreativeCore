package team.creative.creativecore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.Event;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CreativeFabricLoader implements ICreativeLoader {
	public static final net.fabricmc.fabric.api.event.Event<FinishConsuming> FINISH_CONSUMING = EventFactory.createArrayBacked(FinishConsuming.class, callbacks -> (entity, stack) -> {
		for (FinishConsuming callback : callbacks) {
			callback.accept(entity, stack);
		}
	});
	public final List<Runnable> RENDER_START = new ArrayList<>();
	public final List<Consumer> RENDER_GUI = new ArrayList<>();

	@Override
	public void register(CommonLoader loader) {
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
			loader.registerCommands(dispatcher);
		});
	}

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
	public void registerModifyCreativeTab(Consumer<CreativeModeTab.Output> modifier, ResourceKey<CreativeModeTab> tabResourceKey) {
		CreativeModeTabEvents.modifyOutputEvent(tabResourceKey).register(modifier::accept);
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

	@Override
	public boolean forge() {
		return false;
	}

	@Override
	public boolean fabric() {
		return true;
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

	@Override
	public void registerPlayerJoin(Consumer<Player> consumer) {
		ServerPlayerEvents.JOIN.register(consumer::accept);
	}

	@Override
	public void registerPlayerCopy(CopyConsumer consumer) {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> consumer.accept(oldPlayer, newPlayer, !alive));
	}

	@Override
	public void registerPlayerDimensionChange(PlayerDimensionChangeConsumer consumer) {
		ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(((player, origin, destination) -> {
			consumer.accept(player, origin.dimension(), destination.dimension());
		}));
	}

	@Override
	public void registerPlayerRespawn(Consumer<Player> consumer) {
		ServerPlayerEvents.AFTER_RESPAWN.register((ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) -> {
			consumer.accept(newPlayer);
		});
	}

	@Override
	public void registerItemUsed(FinishConsuming consumer) {
		FINISH_CONSUMING.register(consumer);
	}

	@Override
	public void publishItemUsed(LivingEntity entity, ItemStack stack) {
		FINISH_CONSUMING.invoker().accept(entity, stack);
	}

	@Override
	public boolean onItemToss(ItemEntity item, Player player) {
		return false;
	}
}
