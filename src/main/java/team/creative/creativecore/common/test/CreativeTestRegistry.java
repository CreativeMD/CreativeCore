package team.creative.creativecore.common.test;

import team.creative.creativecore.common.test.geo.TestVectorFan;
import team.creative.creativecore.common.util.registry.NamedHandlerRegistry;

public class CreativeTestRegistry {
    
    public static final NamedHandlerRegistry<CreativeTest> REGISTRY = new NamedHandlerRegistry<>(null);
    
    static {
        REGISTRY.register("vectorfan_bounds", new TestVectorFan());
    }
    
}
