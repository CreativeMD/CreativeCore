package team.creative.creativecore.mixin;

import net.minecraft.server.packs.AbstractPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(AbstractPackResources.class)
public interface PathPackResourcesAccessor {
    
    @Accessor("file")
    File getRoot();
    
}
