package team.creative.creativecore.mixin;

import java.util.zip.ZipFile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.packs.FilePackResources;

@Mixin(FilePackResources.class)
public interface FilePackResourcesAccessor {
    
    @Accessor
    public ZipFile getZipFile();
    
}
