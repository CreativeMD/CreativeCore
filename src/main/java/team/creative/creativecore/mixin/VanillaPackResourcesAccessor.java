package team.creative.creativecore.mixin;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;

@Mixin(VanillaPackResources.class)
public interface VanillaPackResourcesAccessor {
    
    @Accessor
    public Map<PackType, List<Path>> getPathsForType();
    
}
