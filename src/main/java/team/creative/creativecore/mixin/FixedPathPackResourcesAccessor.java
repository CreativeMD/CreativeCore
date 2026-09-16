package team.creative.creativecore.mixin;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.packs.FixedPathPackResources;
import net.minecraft.server.packs.PackType;

@Mixin(FixedPathPackResources.class)
public interface FixedPathPackResourcesAccessor {
    
    @Accessor
    public Map<PackType, List<Path>> getPathsForType();
    
}
