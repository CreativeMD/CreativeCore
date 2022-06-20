package team.creative.creativecore.common.gui.style.display;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.style.display.StyleDisplay.StyleDisplayDeserializer;
import team.creative.creativecore.common.util.math.geo.Rect;

@Environment(EnvType.CLIENT)
@OnlyIn(Dist.CLIENT)
@JsonAdapter(value = StyleDisplayDeserializer.class)
public abstract class StyleDisplay {
    
    public static final StyleDisplay NONE = new StyleDisplay() {
        
        @Override
        public void render(PoseStack pose, double x, double y, double width, double height) {}
    };
    
    private static HashMap<String, Class<? extends StyleDisplay>> types = new HashMap<>();
    
    public static void registerType(String id, Class<? extends StyleDisplay> clazz) {
        if (types.containsKey(id))
            throw new IllegalArgumentException(id + " is already taken");
        types.put(id, clazz);
    }
    
    public void render(PoseStack matrix, double width, double height) {
        render(matrix, 0, 0, width, height);
    }
    
    public void render(PoseStack matrix, Rect origin, Rect rect) {
        //render(matrix, rect.minX - origin.minX, rect.minY - origin.minY, rect.getWidth(), rect.getHeight());
        render(matrix, rect.getWidth(), rect.getHeight());
    }
    
    public abstract void render(PoseStack pose, double x, double y, double width, double height);
    
    static {
        registerType("color", DisplayColor.class);
        registerType("tex", DisplayTexture.class);
        registerType("texs", DisplayTextureStretch.class);
    }
    
    public static class StyleDisplayDeserializer implements JsonDeserializer<StyleDisplay> {
        
        @Override
        public StyleDisplay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject())
                throw new JsonParseException("style display cannot be a property");
            if (!json.getAsJsonObject().has("type"))
                throw new JsonParseException("missing type in style display");
            Class<? extends StyleDisplay> clazz = types.get(json.getAsJsonObject().get("type").getAsString());
            if (clazz == null)
                throw new JsonParseException("style display type not found " + json.getAsJsonObject().get("type"));
            return context.deserialize(json, clazz);
        }
        
    }
    
}
