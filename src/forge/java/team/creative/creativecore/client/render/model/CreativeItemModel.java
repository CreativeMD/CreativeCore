package team.creative.creativecore.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CreativeItemModel {
    
    protected final ModelResourceLocation location;
    
    public CreativeItemModel(ModelResourceLocation location) {
        this.location = location;
    }
    
    public void applyCustomOpenGLHackery(PoseStack pose, ItemStack stack, ItemDisplayContext context) {}
    
    public CreativeBakedModel create(CreativeBlockModel block) {
        return new CreativeBakedModel(location, this);
    }
    
}
