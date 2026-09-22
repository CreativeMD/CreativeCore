package team.creative.creativecore.common.mod.sable;

import java.util.Iterator;
import java.util.List;

import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.util.math.box.ABB;

public class SableManager {
    
    public static final String MODID = "sable";
    public static final boolean INSTALLED = CreativeCore.loader().isModLoaded(MODID);
    
    public static Iterator sableBoxes(List<ABB> boxes) {
        if (INSTALLED)
            return SableInteractor.sableBoxes(boxes);
        return null;
    }
    
}
