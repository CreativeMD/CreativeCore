package team.creative.creativecore.client.render.box.bridge;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import team.creative.creativecore.common.level.LevelAccessorFake;

/**
 * Optional bridge that routes {@link BakedModel#getQuads} through the Fabric Renderer API
 * pipeline ({@code FabricBakedModel#emitBlockQuads}) when such an implementation is detected
 * on the classpath. This is the integration point used by mods like Continuity to provide
 * connected-textures support to {@link team.creative.creativecore.client.render.box.RenderBox}
 * and any consumer (e.g. LittleTiles) that goes through it.
 *
 * <p>This entry class intentionally references zero Fabric API symbols so that CreativeCore
 * can still load on a pure NeoForge environment without Forgified Fabric API present.
 * The actual implementation classes ({@code impl/*}) are loaded lazily and only when
 * {@link #FABRIC_RENDERER_PRESENT} is {@code true}.</p>
 */
public final class FabricRendererBridge {
    
    private static final boolean FABRIC_RENDERER_PRESENT = classExists("net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel");
    private static final boolean SODIUM_PRESENT = classExists("net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext");
    
    private FabricRendererBridge() {}
    
    private static boolean classExists(String name) {
        try {
            Class.forName(name, false, FabricRendererBridge.class.getClassLoader());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
    
    /** {@code true} iff the Fabric Renderer API is present at runtime. */
    public static boolean isAvailable() {
        return FABRIC_RENDERER_PRESENT;
    }
    
    /** {@code true} iff Sodium's Fabric Renderer API integration is present at runtime. */
    public static boolean isSodiumPresent() {
        return SODIUM_PRESENT;
    }
    
    /**
     * Attempts to bridge a {@code BakedModel.getQuads} call through the Fabric Renderer
     * pipeline. Returns {@code null} when the bridge is unavailable, the model does not
     * participate, or no usable {@link BlockAndTintGetter} can be resolved.
     */
    @Nullable
    public static List<BakedQuad> tryBridge(BakedModel model, BlockState state, @Nullable Direction direction,
            RandomSource random, ModelData modelData, RenderType layer,
            @Nullable LevelAccessor levelAccessor, BlockPos pos) {
        if (!FABRIC_RENDERER_PRESENT) return null;
        BlockAndTintGetter view = resolveBlockView(levelAccessor);
        if (view == null) return null;
        // FabricBridgeImpl is referenced by FQN (no import) so the symbolic reference is
        // only resolved when this line is actually executed, keeping CreativeCore safe to
        // load on a pure NeoForge classpath without Forgified Fabric API.
        return team.creative.creativecore.client.render.box.bridge.impl.FabricBridgeImpl
                .tryBridge(model, state, direction, random, modelData, layer, view, pos, SODIUM_PRESENT);
    }
    
    /**
     * Default resolver: unwraps a {@link LevelAccessorFake} to its real level, otherwise
     * returns the accessor itself if it already implements {@link BlockAndTintGetter}.
     */
    @Nullable
    private static BlockAndTintGetter resolveBlockView(@Nullable LevelAccessor levelAccessor) {
        if (levelAccessor instanceof LevelAccessorFake fake) {
            return fake.getRealLevel();
        }
        if (levelAccessor instanceof BlockAndTintGetter view) {
            return view;
        }
        return null;
    }
}
