package team.creative.creativecore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;
import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;

public class CreativeForgeLoader implements ICreativeLoader {
    private static final Map<String, DeferredRegister<AttachmentType<?>>> ATTACHMENT_TYPES = new HashMap<>();
    
    @Override
    public Side getOverallSide() {
        return FMLEnvironment.getDist().isClient() ? Side.CLIENT : Side.SERVER;
    }
    
    @Override
    public void register(CommonLoader loader) {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((FMLCommonSetupEvent x) -> loader.onInitialize());
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent x) -> loader.registerCommands(x.getDispatcher()));
    }
    
    @Override
    public void registerClient(ClientLoader loader) {
        if (FMLLoader.getCurrent().getDist() == Dist.CLIENT) {
            ModLoadingContext.get().getActiveContainer().getEventBus().addListener((FMLClientSetupEvent x) -> loader.onInitializeClient());
            NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent x) -> loader.registerClientCommands(x.getDispatcher()));
        }
    }
    
    @Override
    public void registerClientTick(Runnable run) {
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Pre x) -> run.run());
    }
    
    @Override
    public void registerClientRenderGui(Consumer run) {
        NeoForge.EVENT_BUS.addListener((RenderGuiEvent.Post x) -> run.accept(x.getGuiGraphics()));
    }
    
    @Override
    public void registerClientRenderStart(Runnable run) {
        NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Pre x) -> run.run());
    }
    
    @Override
    public void registerReloadListener(Identifier identifier, PreparableReloadListener listener) {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((AddClientReloadListenersEvent x) -> x.addListener(identifier, listener));
    }
    
    @Override
    public void registerLevelTick(Consumer<ServerLevel> consumer) {
        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post x) -> {
            if (x.getLevel() instanceof ServerLevel level)
                consumer.accept(level);
        });
    }
    
    @Override
    public void registerLevelTickStart(Consumer<ServerLevel> consumer) {
        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Pre x) -> {
            if (x.getLevel() instanceof ServerLevel level)
                consumer.accept(level);
        });
        
    }
    
    @Override
    public void registerUnloadLevel(Consumer<LevelAccessor> consumer) {
        NeoForge.EVENT_BUS.addListener((LevelEvent.Unload x) -> consumer.accept(x.getLevel()));
    }
    
    @Override
    public void registerModifyCreativeTab(Consumer<CreativeModeTab.Output> modifier, ResourceKey<CreativeModeTab> tabResourceKey) {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == tabResourceKey) {
                modifier.accept(event);
            }
        });
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
    public float getFriction(LevelAccessor level, BlockPos pos, Entity entity) {
        return level.getBlockState(pos).getFriction(level, pos, entity);
    }
    
    @Override
    public Side getEffectiveSide() {
        return EffectiveSide.get().isClient() ? Side.CLIENT : Side.SERVER;
    }
    
    @Override
    public boolean forge() {
        return true;
    }
    
    @Override
    public MinecraftServer getCurrentServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public void registerPlayerJoin(Consumer<Player> consumer) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> consumer.accept(event.getEntity()));
    }
    
    @Override
    public void registerPlayerCopy(CopyConsumer consumer) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.Clone event) -> consumer.accept(event.getOriginal(), event.getEntity(), event.isWasDeath()));
    }
    
    @Override
    public void registerPlayerDimensionChange(PlayerDimensionChangeConsumer consumer) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> {
            consumer.accept(event.getEntity(), event.getFrom(), event.getTo());
        });
    }
    
    @Override
    public void registerItemUsed(FinishConsuming consumer) {
        NeoForge.EVENT_BUS.addListener((LivingEntityUseItemEvent.Finish event) -> consumer.accept(event.getEntity(), event.getItem()));
    }
    
    @Override
    public void registerPlayerRespawn(Consumer<Player> consumer) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent event) -> consumer.accept(event.getEntity()));
    }
    
    @Override
    public boolean fabric() {
        return false;
    }
    
    @Override
    public void publishItemUsed(LivingEntity entity, ItemStack stack) {
        EventHooks.onItemUseFinish(entity, stack, 0, ItemStack.EMPTY);
    }
    
    @Override
    public <T> ICreativeAttachmentType<T> registerAttachment(Identifier identifier, Supplier<T> supplier, MapCodec<T> mapCodec) {
        var neoAttachment = AttachmentType.builder(supplier).serialize(mapCodec).build();
        var register = ATTACHMENT_TYPES.computeIfAbsent(identifier.getNamespace(), (key) -> {
            var dr = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, key);
            dr.register(ModLoadingContext.get().getActiveContainer().getEventBus());
            return dr;
        });
        register.register(identifier.getPath(), () -> neoAttachment);

        return new ICreativeAttachmentType<T>() {
            @Override
            public T get(Player player) {
                return player.getData(neoAttachment);
            }

            @Override
            public void set(Player player, T value) {
                player.setData(neoAttachment, value);
            }
        };
    }

    @Override
    public void registerRemoveEffectCallback(RemoveEffect consumer) {
        NeoForge.EVENT_BUS.addListener((MobEffectEvent.Remove event) -> {
           if(consumer.shouldCancel(event.getEffectInstance(), event.getEntity())) {
               event.setCanceled(true);
            }
        });
    }

    @Override
    public void registerItemStorage(Function<ItemStack, List<ItemStack>> inventoryGetter, Supplier<ItemLike[]> items) {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((RegisterCapabilitiesEvent event) -> {
            event.registerItem(Capabilities.Item.ITEM, (itemStack, context) -> new ItemStacksResourceHandler(NonNullList.copyOf(inventoryGetter.apply(itemStack))), items.get());
        });
    }

    private record ForgeTransaction(Transaction transaction) implements CommonTransaction {
            public ForgeTransaction(CommonTransaction other) {
                this(other instanceof ForgeTransaction(Transaction transaction1)
                     ? Transaction.open(transaction1) : null);
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

    private record ForgeItemResource(ItemResource resource) implements CommonItemResource {
        @Override
            public boolean isEmpty() {
                return resource.isEmpty();
            }

            @Override
            public Item getItem() {
                return resource.getItem();
            }

            public int getMaxStackSize() {
                return resource.getMaxStackSize();
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

    private record ForgeItemStorageView(ResourceHandler<ItemResource> handler,
                                        int index) implements CommonItemStorageView {
        @Override
            public int getCapacityAsInt() {
                return handler.getCapacityAsInt(index, handler.getResource(index));
            }

            @Override
            public int getAmountAsInt() {
                return handler.getAmountAsInt(index);
            }

            @Override
            public ForgeItemResource getResource() {
                return new ForgeItemResource(handler.getResource(index));
            }

            @Override
            public int insert(CommonItemResource resource, int amount, CommonTransaction transaction) {
                return handler.insert(
                        index,
                        ((ForgeItemResource) resource).resource,
                        amount,
                        ((ForgeTransaction) transaction).transaction
                );
            }

            @Override
            public int extract(CommonItemResource resource, int amount, CommonTransaction transaction) {
                return handler.extract(
                        index,
                        ((ForgeItemResource) resource).resource,
                        amount,
                        ((ForgeTransaction) transaction).transaction
                );
            }
        }

    private record ForgeItemStorage(ResourceHandler<ItemResource> handler) implements CommonItemStorage {

        @Override
            public int insert(CommonItemResource resource, int amount, CommonTransaction transaction) {
                return handler.insert(
                        ((ForgeItemResource) resource).resource,
                        amount,
                        ((ForgeTransaction) transaction).transaction
                );
            }

            @Override
            public int extract(CommonItemResource resource, int amount, CommonTransaction transaction) {
                return handler.extract(
                        ((ForgeItemResource) resource).resource,
                        amount,
                        ((ForgeTransaction) transaction).transaction
                );
            }

            @Override
            public Iterator<CommonItemStorageView> iterator() {
                return new ComputeNextIterator<>() {
                    int i = 0;

                    @Override
                    protected ForgeItemStorageView computeNext() {
                        if (i < handler.size()) {
                            return new ForgeItemStorageView(handler, i++);
                        }
                        end();
                        return null;
                    }
                };
            }
        }

    @Override
    public CommonItemStorage getItemStorage(Player player, InteractionHand hand) {
        return new ForgeItemStorage(player.getItemInHand(hand).getCapability(Capabilities.Item.ITEM, ItemAccess.forPlayerInteraction(player, hand)));
    }

    @Override
    public CommonItemStorage getItemStorage(Level level, BlockPos pos, Direction direction) {
        return new ForgeItemStorage(level.getCapability(Capabilities.Item.BLOCK, pos, direction));
    }

    @Override
    public CommonTransaction openTransaction(CommonTransaction outer) {
        return new ForgeTransaction(outer);
    }

    @Override
    public boolean onItemToss(ItemEntity item, Player player) {
        return NeoForge.EVENT_BUS.post(new ItemTossEvent(item, player)).isCanceled();
    }

    @Override
    public void register(CommonRegistry registry) {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((RegisterEvent event) -> {
            registry.addEntries(new CommonRegistry.RegisterHelper() {
                @Override
                public <T, I extends T> void register(Registry<T> registry, ResourceKey<T> name, Supplier<I> value) {
                    event.register(registry.key(), name.identifier(), value::get);
                }
            });
        });
    }
}
