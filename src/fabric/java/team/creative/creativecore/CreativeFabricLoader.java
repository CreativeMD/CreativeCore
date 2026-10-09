package team.creative.creativecore;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.Event;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;
import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;

public class CreativeFabricLoader implements ICreativeLoader {
    private static MinecraftServer currentServer = null;
    public static final net.fabricmc.fabric.api.event.Event<FinishConsuming> FINISH_CONSUMING = EventFactory.createArrayBacked(FinishConsuming.class, callbacks -> (entity, stack) -> {
        for (FinishConsuming callback : callbacks) {
            callback.accept(entity, stack);
        }
    });
    static {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            currentServer = server;
        });

        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            currentServer = null;
        });
    }

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
        ServerTickEvents.END_LEVEL_TICK.register(consumer::accept);
    }

    @Override
    public void registerLevelTickStart(Consumer<ServerLevel> consumer) {
        ServerTickEvents.START_LEVEL_TICK.register(consumer::accept);
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
    public MinecraftServer getCurrentServer() {
        return currentServer;
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
    public <T> ICreativeAttachmentType<T> registerAttachment(Identifier identifier, Supplier<T> supplier, MapCodec<T> mapCodec) {
        AttachmentType<T> type = AttachmentRegistry.create(identifier, builder -> { builder.initializer(supplier).persistent(mapCodec.codec()); });
        return new ICreativeAttachmentType<T>() {
            @Override
            public T get(Player player) {
                return player.getAttachedOrCreate(type);
            }

            @Override
            public void set(Player player, T value) {
                player.setAttached(type,value);
            }
        };
    }

    @Override
    public void registerRemoveEffectCallback(RemoveEffect consumer) {
        ServerMobEffectEvents.ALLOW_EARLY_REMOVE.register((effectInstance, entity, ctx) -> !consumer.shouldCancel(effectInstance,entity));
    }

    public static class FabricTransaction implements CommonTransaction {
        private final Transaction transaction;
        public FabricTransaction(CommonTransaction other) {
            if(other instanceof FabricTransaction cOther) {
                this.transaction = Transaction.openNested(cOther.transaction);
            } else {
                this.transaction = Transaction.openNested(null);
            }
        }

        @Override
        public void commit() {
            transaction.commit();
        }

        @Override
        public void close() {
            transaction.close();
        }
    }

    private static class FabricItemResource implements CommonItemResource {
        private final ItemVariant resource;
        public FabricItemResource(ItemVariant resource) {
            this.resource = resource;
        }
        @Override
        public boolean isEmpty() {
            return resource.isBlank();
        }

        @Override
        public int getMaxStackSize() {
            return resource.toStack().getMaxStackSize();
        }

        @Override
        public Item getItem() {
            return resource.getItem();
        }

        @Override
        public ItemStack toStack(int amount) {
            return resource.toStack(amount);
        }
        @Override
        public ItemStack toStack() {
            return resource.toStack();
        }

        @Override
        public Holder<Item> typeHolder() {
            return resource.typeHolder();
        }

        @Override
        public DataComponentMap getComponents() {
            return resource.getComponents();
        }
    }

    public static class FabricItemStorageView implements CommonItemStorageView {
        private final StorageView<ItemVariant> view;
        private final Storage<ItemVariant> parent;
        public FabricItemStorageView(StorageView<ItemVariant> view, Storage<ItemVariant> parent) {
            this.view = view;
            this.parent = parent;
        }

        @Override
        public int getCapacityAsInt() {
            return (int) view.getCapacity();
        }

        @Override
        public CommonItemResource getResource() {
            return new FabricItemResource(view.getResource());
        }

        @Override
        public int getAmountAsInt() {
            return (int) view.getAmount();
        }

        @Override
        public int insert(CommonItemResource resource, int amount, CommonTransaction transaction) {
            if(view instanceof Storage storage) {
                return (int) storage.insert(((FabricItemResource) resource).resource, amount, ((FabricTransaction) transaction).transaction);
            } else {
                return (int) parent.insert(((FabricItemResource) resource).resource, amount, ((FabricTransaction) transaction).transaction);
            }
        }

        @Override
        public int extract(CommonItemResource resource, int amount, CommonTransaction transaction) {
            return (int) view.extract(((FabricItemResource) resource).resource, amount, ((FabricTransaction) transaction).transaction);
        }
    }

    private static class FabricItemStorage implements CommonItemStorage {
        private final SlottedStorage<ItemVariant> handler;
        public FabricItemStorage(SlottedStorage<ItemVariant> handler) {
            this.handler = handler;
        }
        @Override
        public int insert(CommonItemResource resource, int amount, CommonTransaction transaction) {
            return (int) handler.insert(((FabricItemResource) resource).resource, amount, ((FabricTransaction) transaction).transaction);
        }

        @Override
        public int extract(CommonItemResource resource, int amount, CommonTransaction transaction) {
            return (int) handler.extract(((FabricItemResource) resource).resource, amount, ((FabricTransaction) transaction).transaction);
        }

        @Override
        public Iterator<CommonItemStorageView> iterator() {
            return new ComputeNextIterator<>() {
                Iterator<StorageView<ItemVariant>> iterator = handler.iterator();
                @Override
                protected FabricItemStorageView computeNext() {
                    if(iterator.hasNext()) {
                        return new FabricItemStorageView(iterator.next(), handler);
                    }
                    end();
                    return null;
                }
            };
        }
    }

    @Override
    public CommonTransaction openTransaction(CommonTransaction outer) {
        return new FabricTransaction(outer);
    }

    @Override
    public void registerItemStorage(Function<ItemStack, List<ItemStack>> inventoryGetter, Supplier<ItemLike[]> items) {
        ItemStorage.ITEM.registerForItems(
                (itemStack, context) -> {
                    return ContainerStorage.of(
                            new SimpleContainer(inventoryGetter.apply(itemStack)
                                                               .toArray(new ItemStack[0])), null
                    );
                }, items.get()
        );
    }

    @Override
    public CommonItemStorage getItemStorage(Player player, InteractionHand hand) {
        var unSlottedStorage = ContainerItemContext.forPlayerInteraction(player, hand).find(ItemStorage.ITEM);
        if(unSlottedStorage instanceof SlottedStorage<ItemVariant> slottedStorage) {
            return new FabricItemStorage(slottedStorage);
        }
        return null;
    }

    @Override
    public CommonItemStorage getItemStorage(Level level, BlockPos pos, Direction direction) {
        var unSlottedStorage = ItemStorage.SIDED.find(level, pos, direction);
        if(unSlottedStorage instanceof SlottedStorage<ItemVariant> slottedStorage) {
            return new FabricItemStorage(slottedStorage);
        }
        return null;
    }

    @Override
    public void register(CommonRegistry registry) {
        registry.addEntries(new CommonRegistry.RegisterHelper() {
            @Override
            public <T, I extends T> void register(Registry<T> registry, ResourceKey<T> name, Supplier<I> value) {
                Registry.register(registry, name, value.get());
            }
        });
    }
    
    @Override
    public boolean onItemToss(ItemEntity item, Player player) {
        return false;
    }
}
