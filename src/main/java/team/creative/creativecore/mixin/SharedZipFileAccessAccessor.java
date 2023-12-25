package team.creative.creativecore.mixin;

import java.util.zip.ZipFile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.packs.FilePackResources.SharedZipFileAccess;

@Mixin(SharedZipFileAccess.class)
public interface SharedZipFileAccessAccessor {
    
    @Invoker
    public ZipFile callGetOrCreateZipFile();
}
