package team.creative.creativecore.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.resource.DelegatingPackResources;

@Mixin(DelegatingPackResources.class)
public interface DelegatingPackResourcesAccessor {
    
    @Invoker
    public List<PackResources> callGetCandidatePacks(PackType type, ResourceLocation location);
    
}
