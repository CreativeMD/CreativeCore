package team.creative.creativecore.common.mod.sable;

import java.util.Iterator;
import java.util.List;

import net.neoforged.fml.ModList;
import team.creative.creativecore.common.util.math.box.ABB;

public class SableManager {
    
    public static final String MODID = "sable";
    public static final boolean INSTALLED = ModList.get().isLoaded(MODID);
    
    public static Iterator sableBoxes(List<ABB> boxes) {
        if (INSTALLED)
            return SableInteractor.sableBoxes(boxes);
        return null;
    }
    
}
