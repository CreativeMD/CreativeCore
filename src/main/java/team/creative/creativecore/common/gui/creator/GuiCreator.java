package team.creative.creativecore.common.gui.creator;

import java.util.function.BiFunction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.GuiScreenIntegration;
import team.creative.creativecore.common.gui.packet.OpenGuiPacket;
import team.creative.creativecore.common.util.mc.PlayerUtils;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

public abstract class GuiCreator {
    
    public static final NamedHandlerRegistry<GuiCreator> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static final GuiCreatorItem ITEM_OPENER = (GuiCreatorItem) GuiCreator.register("item", new GuiCreatorItem()).disableClient();
    public static final GuiCreatorBlock BLOCK_OPENER = (GuiCreatorBlock) GuiCreator.register("block", new GuiCreatorBlock()).disableClient();
    
    public static <T extends GuiCreator> T register(String name, T creator) {
        creator.name = name;
        REGISTRY.register(name, creator);
        return creator;
    }
    
    String name;
    public final BiFunction<CompoundTag, Player, GuiLayer> function;
    private boolean supportsClient = true;
    
    public GuiCreator(BiFunction<CompoundTag, Player, GuiLayer> function) {
        this.function = function;
    }
    
    public String getName() {
        return name;
    }
    
    public GuiCreator disableClient() {
        this.supportsClient = true;
        return this;
    }
    
    protected void openGui(CompoundTag nbt, Player player) {
        if (player.level().isClientSide) {
            if (supportsClient)
                CreativeCore.NETWORK.sendToServer(new OpenGuiPacket(name, nbt));
        } else
            OpenGuiPacket.openGuiOnServer(this, nbt, (ServerPlayer) player);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public static void openClientSide(GuiLayer layer) {
        Minecraft.getInstance().forceSetScreen(new GuiScreenIntegration(layer));
    }
    
    public static class GuiCreatorBasic extends GuiCreator {
        
        public GuiCreatorBasic(BiFunction<CompoundTag, Player, GuiLayer> function) {
            super(function);
        }
        
        public void open(Player player) {
            openGui(new CompoundTag(), player);
        }
        
        public void open(CompoundTag nbt, Player player) {
            openGui(nbt, player);
        }
        
    }
    
    public static class GuiCreatorItem extends GuiCreator {
        
        public GuiCreatorItem() {
            super((nbt, player) -> {
                InteractionHand hand = nbt.getBoolean("main_hand") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof ItemGuiCreator item)
                    return item.create(nbt, player);
                return null;
            });
        }
        
        public void open(Player player, InteractionHand hand) {
            open(new CompoundTag(), player, hand);
        }
        
        public void open(CompoundTag nbt, Player player, InteractionHand hand) {
            nbt.putBoolean("main_hand", hand == InteractionHand.MAIN_HAND);
            openGui(nbt, player);
        }
    }
    
    public static class GuiCreatorBlock extends GuiCreator {
        
        public GuiCreatorBlock() {
            super((nbt, player) -> {
                BlockPos pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
                Level level = player.level();
                if (level.isLoaded(pos) && canInteractWithBlock(player, pos, 1)) {
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof BlockGuiCreator block)
                        return block.create(nbt, level, pos, state, player);
                }
                return null;
            });
        }
        
        public static boolean canInteractWithBlock(Player player, BlockPos pos, double extraRange) {
            double d0 = PlayerUtils.getReach(player) + extraRange;
            return new AABB(pos).distanceToSqr(player.getEyePosition()) < d0 * d0;
        }
        
        public void open(Player player, BlockPos pos) {
            open(new CompoundTag(), player, pos);
        }
        
        public void open(CompoundTag nbt, Player player, BlockPos pos) {
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());
            openGui(nbt, player);
        }
    }
    
}
