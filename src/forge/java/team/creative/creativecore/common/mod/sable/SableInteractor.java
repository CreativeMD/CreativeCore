package team.creative.creativecore.common.mod.sable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import team.creative.creativecore.common.util.math.box.ABB;

public class SableInteractor {
    
    public static Iterator sableBoxes(List<ABB> boxes) {
        List<BoundingBox3d> result = new ArrayList<>(boxes.size());
        for (ABB abb : boxes)
            result.add(new BoundingBox3d(abb.minX, abb.minY, abb.minZ, abb.maxX, abb.maxY, abb.maxZ));
        return result.iterator();
    }
    
}
