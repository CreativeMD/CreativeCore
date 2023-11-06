package team.creative.creativecore.mixin;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.minecraft.server.packs.PackResources;

@Mixin(GroupResourcePack.class)
public interface GroupResourcePackAccessor {
    
    @Accessor
    public Map<String, List<PackResources>> getNamespacedPacks();
    
}
