package team.creative.creativecore.common.util.mc;

import java.nio.file.Path;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import team.creative.creativecore.mixin.PathPackResourcesAccessor;

public class PlatformResourceUtils {
    
    public static Path resolvePath(PackType type, PackResources source, ResourceLocation location, List<String> parts) {
        if (source instanceof PathPackResourcesAccessor forge) {
            Path path = forge.getRoot().resolve(type.getDirectory()).resolve(location.getNamespace());
            for (String name : parts)
                path = path.resolve(name);
            return path;
        }
        return null;
    }
    
}
