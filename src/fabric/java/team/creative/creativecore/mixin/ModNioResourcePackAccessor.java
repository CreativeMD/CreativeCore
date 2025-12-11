package team.creative.creativecore.mixin;

import java.nio.file.Path;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.fabric.impl.resource.pack.ModNioPackResources;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

@Mixin(ModNioPackResources.class)
public interface ModNioResourcePackAccessor {
    
    @Invoker
    public static String callGetFilename(PackType type, Identifier id) {
        throw new UnsupportedOperationException();
    }
    
    @Invoker
    public Path callGetPath(String filename);
}
