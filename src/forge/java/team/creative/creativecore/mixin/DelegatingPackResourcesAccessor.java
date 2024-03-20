package team.creative.creativecore.mixin;

import java.util.List;

import net.minecraftforge.resource.DelegatingResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

@Mixin(DelegatingResourcePack.class)
public interface DelegatingPackResourcesAccessor {
    
    @Invoker(remap = false)
    public List<PackResources> callGetCandidatePacks(PackType type, ResourceLocation location);
    
}
