package team.creative.creativecore.mixin;

import java.nio.file.Path;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.packs.PathPackResources;

@Mixin(PathPackResources.class)
public interface PathPackResourcesAccessor {
    
    @Accessor
    public Path getRoot();
    
}
