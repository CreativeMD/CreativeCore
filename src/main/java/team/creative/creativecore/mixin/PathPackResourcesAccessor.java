package team.creative.creativecore.mixin;

import java.io.File;

import net.minecraft.server.packs.AbstractPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractPackResources.class)
public interface PathPackResourcesAccessor {
    
    @Accessor("file")
    File getRoot();
}
