package team.creative.creativecore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FilePackResources.SharedZipFileAccess;
import net.minecraft.server.packs.PackType;

@Mixin(FilePackResources.class)
public interface FilePackResourcesAccessor {
    
    @Invoker
    public static String callGetPathFromLocation(PackType type, ResourceLocation location) {
        throw new UnsupportedOperationException();
    }
    
    @Accessor
    public SharedZipFileAccess getZipFileAccess();
    
}
