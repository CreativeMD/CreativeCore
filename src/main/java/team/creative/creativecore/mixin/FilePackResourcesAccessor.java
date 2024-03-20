package team.creative.creativecore.mixin;

import java.util.zip.ZipFile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;

@Mixin(FilePackResources.class)
public interface FilePackResourcesAccessor {
    
    @Accessor
    public ZipFile getZipFile();
    
}
