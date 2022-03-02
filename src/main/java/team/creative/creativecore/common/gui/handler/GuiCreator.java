package team.creative.creativecore.common.gui.handler;

import java.util.function.BiFunction;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.integration.GuiScreenIntegration;
import team.creative.creativecore.common.gui.packet.OpenGuiPacket;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

public abstract class GuiCreator {
    
    public static final NamedHandlerRegistry<GuiCreator> REGISTRY = new NamedHandlerRegistry<>(null);
    
    public static final GuiCreatorItem ITEM_OPENER = GuiCreator.register("item", new GuiCreatorItem());
    
    public static <T extends GuiCreator> T register(String name, T creator) {
        creator.name = name;
        REGISTRY.register(name, creator);
        return creator;
    }
    
    String name;
    public final BiFunction<CompoundTag, Player, GuiLayer> function;
    
    public GuiCreator(BiFunction<CompoundTag, Player, GuiLayer> function) {
        this.function = function;
    }
    
    public String getName() {
        return name;
    }
    
    protected void openGui(CompoundTag nbt, Player player) {
        if (player.level.isClientSide)
            CreativeCore.NETWORK.sendToServer(new OpenGuiPacket(name, nbt));
        else
            OpenGuiPacket.openGuiOnServer(this, nbt, (ServerPlayer) player);
    }
    
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
        }
    }
    
}
