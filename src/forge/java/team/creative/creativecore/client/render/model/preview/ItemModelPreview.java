package team.creative.creativecore.client.render.model.preview;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ItemModelPreview {
    
    public ItemStack getPreview(ItemStack stack);
    
}