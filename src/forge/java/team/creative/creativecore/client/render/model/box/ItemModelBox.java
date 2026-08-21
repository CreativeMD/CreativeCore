package team.creative.creativecore.client.render.model.box;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.client.render.box.RenderBox;

@OnlyIn(Dist.CLIENT)
public abstract class ItemModelBox {
    
    public BakedModel resolve(ItemStack stack) {
        return null;
    }
    
    public abstract List<? extends RenderBox> getBoxes(ItemStack stack, boolean translucent);
    
    public boolean checkTranslucentLayer(ItemStack stack) {
        return false;
    }
    
    public boolean hasTranslucentLayer(ItemStack stack) {
        return false;
    }
    
    public List<BakedQuad> getCachedModel(boolean translucent, ItemStack stack, boolean threaded) {
        return null;
    }
    
    public void saveCachedModel(boolean translucent, List<BakedQuad> cachedQuads, ItemStack stack, boolean threaded) {}
    
    public void reload() {}
    
}