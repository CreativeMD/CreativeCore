package team.creative.creativecore.common.util.mc;

import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NBTUtils {
    
    public static CompoundTag mergeNotOverwrite(CompoundTag base, CompoundTag toInsert) {
        for (String id : toInsert.keySet()) {
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
    
    public static CompoundTag of(JsonObject o) {
        return of(o, new CompoundTag());
    }
    
    public static CompoundTag of(JsonObject o, CompoundTag compound) {
        for (Entry<String, JsonElement> entry : o.entrySet())
            compound.put(entry.getKey(), of(entry.getValue()));
        return compound;
    }
    
    public static Tag of(JsonElement element) {
        if (element instanceof JsonPrimitive p) {
            if (p.isBoolean())
                return ByteTag.valueOf(p.getAsBoolean());
            else if (p.isNumber()) {
                Number n = p.getAsNumber();
                if (n instanceof Double)
                    return DoubleTag.valueOf(n.doubleValue());
                else if (n instanceof Float)
                    return FloatTag.valueOf(n.floatValue());
                else if (n instanceof Long)
                    return LongTag.valueOf(n.longValue());
                else if (n instanceof Byte)
                    return ByteTag.valueOf(n.byteValue());
                else if (n instanceof Short)
                    return ShortTag.valueOf(n.shortValue());
                else
                    return IntTag.valueOf(n.intValue());
            }
            return StringTag.valueOf(p.getAsString());
        }
        
        if (element instanceof JsonArray a) {
            ListTag list = new ListTag();
            for (int i = 0; i < a.size(); i++)
                list.add(of(a.get(i)));
            if (list.getFirst() instanceof ByteTag) {
                byte[] bytes = new byte[list.size()];
                for (int i = 0; i < bytes.length; i++)
                    bytes[i] = ((ByteTag) list.get(i)).byteValue();
                return new ByteArrayTag(bytes);
            }
            if (list.getFirst() instanceof IntTag) {
                int[] ints = new int[list.size()];
                for (int i = 0; i < ints.length; i++)
                    ints[i] = ((IntTag) list.get(i)).intValue();
                return new IntArrayTag(ints);
            }
            if (list.getFirst() instanceof LongTag) {
                long[] longs = new long[list.size()];
                for (int i = 0; i < longs.length; i++)
                    longs[i] = ((LongTag) list.get(i)).longValue();
                return new LongArrayTag(longs);
            }
            return list;
        }
        
        return of(element.getAsJsonObject());
    }
}
