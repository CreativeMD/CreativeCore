package team.creative.creativecore.common.gui.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import team.creative.creativecore.CreativeCoreGuiRegistry;
import team.creative.creativecore.common.gui.GuiLayer;

public class GuiManagerItem extends GuiManager {
    
    private ItemStack hand = ItemStack.EMPTY;
    
    private boolean handChanged;
    public SlotAccess handAccess = new SlotAccess() {
        @Override
        public ItemStack get() {
            return getHand();
        }
        
        @Override
        public boolean set(ItemStack hand) {
            setHand(hand);
            return true;
        }
    };
    
    public GuiManagerItem(GuiLayer layer) {
        super(layer);
    }
    
    @Override
    public void tick() {
        if (handChanged) {
            handChanged = false;
            if (!layer.isClient())
                CreativeCoreGuiRegistry.HAND.send(layer, (CompoundTag) ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, hand).getOrThrow());
        }
        super.tick();
    }
    
    @Override
    public void closed() {
        if (!layer.isClient() && !hand.isEmpty() && !layer.getPlayer().addItem(hand))
            layer.getPlayer().drop(hand, false);
        super.closed();
    }
    
    public ItemStack getHand() {
        return hand;
    }
    
    public void setHandChanged() {
        handChanged = true;
    }
    
    public void setHand(ItemStack stack) {
        this.hand = stack;
        setHandChanged();
    }
    
}
