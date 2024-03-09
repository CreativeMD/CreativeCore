package team.creative.creativecore.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.zip.ZipFile;

@Mixin(FilePackResources.class)
public interface FilePackResourcesAccessor {
    
    @Invoker
    public static String callGetPathFromLocation(PackType type, ResourceLocation location) {
        throw new UnsupportedOperationException();
    }
    
    @Accessor
    public ZipFile getZipFile();
    
}
