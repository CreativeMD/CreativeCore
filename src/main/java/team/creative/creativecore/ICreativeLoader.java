package team.creative.creativecore;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
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
import org.jspecify.annotations.Nullable;
import team.creative.creativecore.client.ClientLoader;
import team.creative.creativecore.common.CommonLoader;
import team.creative.creativecore.common.gui.dialog.GuiDialogHandler;

public interface ICreativeLoader {
    
    public default void loadCommon() {
        CreativeCoreGuiRegistry.init();
        GuiDialogHandler.init();
    }

    public MinecraftServer getCurrentServer();
    
    public boolean forge();
    
    public boolean fabric();
    
    public Side getOverallSide();
    
    public Side getEffectiveSide();
    
    public void register(CommonLoader loader);
    
    public void registerClient(ClientLoader loader);
    
    public void registerClientTick(Runnable run);
    
    public void registerClientRenderStart(Runnable run);
    
    public void registerClientRenderGui(Consumer run);
    
    public void registerClientStarted(Runnable run);
    
    public void registerReloadListener(Identifier location, PreparableReloadListener listener);

    public void registerLevelTick(Consumer<ServerLevel> consumer);
    
    public void registerLevelTickStart(Consumer<ServerLevel> consumer);
    
    public void registerLoadLevel(Consumer<LevelAccessor> consumer);
    
    public void registerUnloadLevel(Consumer<LevelAccessor> consumer);
    
    public void registerModifyCreativeTab(Consumer<CreativeModeTab.Output> modifier, ResourceKey<CreativeModeTab> tabResourceKey);
    
    public <T> void registerListener(Consumer<T> consumer);
    
    public float getFluidViscosityMultiplier(Fluid fluid, Level level);
    
    public float getFriction(LevelAccessor level, BlockPos pos, Entity entity);
    
    public void postForge(Event event);
    
    public void registerPlayerJoin(Consumer<Player> consumer);
    
    public void registerPlayerCopy(CopyConsumer consumer);
    
    @FunctionalInterface
    public interface CopyConsumer {
        public void accept(Player oldPlayer, Player newPlayer, boolean wasDeath);
    }
    
    @FunctionalInterface
    public interface PlayerDimensionChangeConsumer {
        public void accept(Player player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim);
    }
    
    @FunctionalInterface
    public interface FinishConsuming {
        void accept(LivingEntity entity, ItemStack stack);
    }

    public <T> ICreativeAttachmentType<T> registerAttachment(Identifier identifier, Supplier<T> supplier, MapCodec<T> mapCodec);

    @FunctionalInterface
    public interface RemoveEffect {
        boolean shouldCancel(MobEffectInstance effectInstance, LivingEntity entity);
    }

    interface CommonTransaction extends AutoCloseable {
        // Commit the transaction
        void commit();
        // Close the transaction if it hasn't been committed.
        @Override
        void close();
    }

    interface CommonItemStorageView {
        int getCapacityAsInt();
        CommonItemResource getResource();
        int getAmountAsInt();

        int insert(CommonItemResource resource, int amount, CommonTransaction transaction);
        int extract(CommonItemResource resource, int amount, CommonTransaction transaction);
    }

    interface CommonItemResource extends DataComponentHolder, TypedInstance<Item> {
        boolean isEmpty();
        Item getItem();
        int getMaxStackSize();
        ItemStack toStack(int amount);
        ItemStack toStack();
    }

    interface CommonItemStorage extends Iterable<CommonItemStorageView> {
        int insert(CommonItemResource resource, int amount, CommonTransaction transaction);
        int extract(CommonItemResource resource, int amount, CommonTransaction transaction);
    }

    public CommonTransaction openTransaction(@Nullable CommonTransaction outer);

    public void registerItemStorage(Function<ItemStack, List<ItemStack>> inventoryGetter, Supplier<ItemLike[]> items);

    public CommonItemStorage getItemStorage(Player player, InteractionHand hand);

    public CommonItemStorage getItemStorage(Level level, BlockPos pos, @Nullable Direction direction);

    public void registerRemoveEffectCallback(RemoveEffect consumer);

    public void publishItemUsed(LivingEntity entity, ItemStack stack);
    
    public void registerItemUsed(FinishConsuming consumer);
    
    public void registerPlayerRespawn(Consumer<Player> consumer);
    
    public void registerPlayerDimensionChange(PlayerDimensionChangeConsumer consumer);
    
    public boolean onItemToss(ItemEntity item, Player player);
    
    public void register(CommonRegistry registry);

    public boolean isModLoaded(String modid);
    
}
