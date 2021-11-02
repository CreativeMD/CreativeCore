package team.creative.creativecore.common.util.mc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class NBTUtils {
    
    public static CompoundTag mergeNotOverwrite(CompoundTag base, CompoundTag toInsert) {
        for (String id : toInsert.getAllKeys()) {
            Tag toInsertEntry = toInsert.get(id);
            if (toInsertEntry == null)
                continue;
            Tag entry = base.get(id);
            if (entry instanceof CompoundTag && toInsertEntry instanceof CompoundTag)
                mergeNotOverwrite((CompoundTag) entry, (CompoundTag) toInsertEntry);
            else if (entry == null)
                base.put(id, toInsertEntry);
        }
        return base;
    }
    
}
