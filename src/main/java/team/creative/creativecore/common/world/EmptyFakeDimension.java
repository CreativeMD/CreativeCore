package team.creative.creativecore.common.world;

import java.util.OptionalLong;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.DefaultBiomeMagnifier;

public class EmptyFakeDimension extends DimensionType {
    
    public EmptyFakeDimension() {
        super(OptionalLong.empty(), true, false, false, false, 1, false, true, true, false, false, 256, DefaultBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD
                .getName(), OVERWORLD_EFFECTS, 0.0F);
    }
    
}
