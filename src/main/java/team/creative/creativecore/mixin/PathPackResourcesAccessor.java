package team.creative.creativecore.mixin;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.packs.AbstractPackResources;

@Mixin(AbstractPackResources.class)
public interface PathPackResourcesAccessor {
    
    @Accessor("file")
    File getRoot();
    
}
