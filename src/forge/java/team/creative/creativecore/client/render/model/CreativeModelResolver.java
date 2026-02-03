package team.creative.creativecore.client.render.model;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface CreativeModelResolver {
    
    @Nullable
    public BakedModel resolve(BakedModel original, ItemStack stack, ClientLevel level, LivingEntity entity, int light);
    
}
