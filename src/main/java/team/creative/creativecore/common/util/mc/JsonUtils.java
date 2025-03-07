package team.creative.creativecore.common.util.mc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class JsonUtils {
    
    public static void set(JsonObject root, String[] path, String key, JsonElement element) {
        get(root, path).add(key, element);
    }
    
    public static JsonObject tryGet(JsonObject root, String[] path) {
        JsonObject current = root;
        for (int i = 0; i < path.length; i++) {
            if (current.has(path[i])) {
                JsonElement e = current.get(path[i]);
                if (e.isJsonObject())
                    current = (JsonObject) e;
                else
                    throw new RuntimeException("Could not create path " + Arrays.toString(path) + " in " + root);
            } else
                return null;
        }
        return current;
    }
    
    public static JsonObject get(JsonObject root, String[] path) {
        JsonObject current = root;
        for (int i = 0; i < path.length; i++) {
            if (current.has(path[i])) {
                JsonElement e = current.get(path[i]);
                if (e.isJsonObject())
                    current = (JsonObject) e;
                else
                    throw new RuntimeException("Could not create path " + Arrays.toString(path) + " in " + root);
            } else {
                JsonObject newObject = new JsonObject();
                current.add(path[i], newObject);
                current = newObject;
            }
        }
        return current;
    }
    
    public static boolean cleanUp(JsonObject json) {
        for (Iterator iterator = json.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, JsonElement> type = (Entry<String, JsonElement>) iterator.next();
            if (type.getValue() instanceof JsonObject && cleanUp((JsonObject) type.getValue()))
                iterator.remove();
        }
        return json.size() == 0;
    }
    
    public static JsonObject of(CompoundTag compound) {
        JsonObject o = new JsonObject();
        for (String id : compound.keySet())
            o.add(id, of(compound.get(id)));
        return o;
    }
    
    public static JsonElement of(Tag tag) {
        if (tag instanceof DoubleTag d)
            return new JsonPrimitive(d.doubleValue());
        if (tag instanceof FloatTag f)
            return new JsonPrimitive(f.floatValue());
        if (tag instanceof LongTag l)
            return new JsonPrimitive(l.longValue());
        if (tag instanceof ShortTag s)
            return new JsonPrimitive(s.shortValue());
        if (tag instanceof IntTag i)
            return new JsonPrimitive(i.intValue());
        if (tag instanceof StringTag s)
            return new JsonPrimitive(s.asString().get());
        if (tag instanceof ByteTag b) {
            byte value = b.asByte().get();
            if (value == 0 || value == 1)
                return new JsonPrimitive(value != 0);
            else
                return new JsonPrimitive(value);
        }
        if (tag instanceof EndTag)
            throw new IllegalArgumentException("End tag is not supported in JSON");
        if (tag instanceof ByteArrayTag bArray) {
            JsonArray array = new JsonArray(bArray.size());
            byte[] content = bArray.getAsByteArray();
            for (int i = 0; i < content.length; i++)
                array.add(content[i]);
            return array;
        }
        if (tag instanceof IntArrayTag iArray) {
            JsonArray array = new JsonArray(iArray.size());
            int[] content = iArray.getAsIntArray();
            for (int i = 0; i < iArray.size(); i++)
                array.add(content[i]);
            return array;
        }
        if (tag instanceof LongArrayTag lArray) {
            JsonArray array = new JsonArray(lArray.size());
            long[] content = lArray.getAsLongArray();
            for (int i = 0; i < lArray.size(); i++)
                array.add(content[i]);
            return array;
        }
        if (tag instanceof ListTag l) {
            JsonArray array = new JsonArray(l.size());
            for (int i = 0; i < l.size(); i++)
                array.add(of(l.get(i)));
            return array;
        }
        return of((CompoundTag) tag);
    }
    
}
