package com.creativemd.creativecore.common.utils.mc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    
}
