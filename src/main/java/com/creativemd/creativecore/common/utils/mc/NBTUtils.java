package com.creativemd.creativecore.common.utils.mc;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUtils {
    
    public static NBTTagCompound mergeNotOverwrite(NBTTagCompound base, NBTTagCompound toInsert) {
        for (String id : toInsert.getKeySet()) {
            NBTBase toInsertEntry = toInsert.getTag(id);
            if (toInsertEntry == null)
                continue;
            NBTBase entry = base.getTag(id);
            if (entry instanceof NBTTagCompound && toInsertEntry instanceof NBTTagCompound)
                mergeNotOverwrite((NBTTagCompound) entry, (NBTTagCompound) toInsertEntry);
            else if (entry == null)
                base.setTag(id, toInsertEntry);
        }
        return base;
    }
    
}
