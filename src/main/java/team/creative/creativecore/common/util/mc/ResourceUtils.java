package team.creative.creativecore.common.util.mc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.Resource;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.mixin.FilePackResourcesAccessor;
import team.creative.creativecore.mixin.PathPackResourcesAccessor;
import team.creative.creativecore.mixin.VanillaPackResourcesAccessor;

public class ResourceUtils {
    
    public static long length(PackType type, Resource resource, ResourceLocation location) {
        return length(type, resource.source(), location);
    }
    
    public static long length(PackType type, PackResources source, ResourceLocation location) {
        if (source instanceof FilePackResourcesAccessor zip) {
            var entry = zip.getZipFile().getEntry(FilePackResourcesAccessor.callGetPathFromLocation(type, location));
            if (entry != null)
                return entry.getSize();
            return 0;
        }
        
        long length = PlatformResourceUtils.length(type, source, location);
        if (length > 0)
            return length;
        
        Path path = FileUtil.decomposePath(location.getPath()).get().map(x -> {
            if (source instanceof VanillaPackResourcesAccessor vanilla)
                return resolve(vanilla.getPathsForType().get(type), location, x);
            
            if (source instanceof PathPackResourcesAccessor pack)
                return FileUtil.resolvePath(pack.getRoot().resolve(type.getDirectory()).resolve(location.getNamespace()), x);
            
            return PlatformResourceUtils.resolvePath(type, source, location, x);
        }, x -> {
            CreativeCore.LOGGER.error("Invalid path {}: {}", location, x.message());
            return null;
        });
        if (path != null)
            return path.toFile().length();
        return 0;
    }
    
    private static Path resolve(List<Path> rootPaths, ResourceLocation location, List<String> parts) {
        for (Path path : rootPaths) {
            Path path1 = FileUtil.resolvePath(path.resolve(location.getNamespace()), parts);
            if (Files.exists(path1) && PathPackResources.validatePath(path1))
                return path1;
        }
        return null;
    }
}
