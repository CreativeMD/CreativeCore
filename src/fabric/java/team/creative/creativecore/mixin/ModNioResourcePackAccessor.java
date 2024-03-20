package team.creative.creativecore.mixin;

import java.nio.file.Path;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

@Mixin(ModNioResourcePack.class)
public interface ModNioResourcePackAccessor {
    
    @Invoker
    public static String callGetFilename(PackType type, ResourceLocation id) {
        throw new UnsupportedOperationException();
    }
    
    @Invoker
    public Path callGetPath(String filename);
}
