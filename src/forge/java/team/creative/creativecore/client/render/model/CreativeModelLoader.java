package team.creative.creativecore.client.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class CreativeModelLoader implements IGeometryLoader<CreativeUnbakedModel> {
    
    @Override
    public CreativeUnbakedModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        ResourceLocation block = null;
        if (jsonObject.has("block"))
            block = new ResourceLocation(jsonObject.get("block").getAsString());
        ResourceLocation item = null;
        if (jsonObject.has("item"))
            item = new ResourceLocation(jsonObject.get("item").getAsString());
        return new CreativeUnbakedModel(item, block);
    }
    
}
