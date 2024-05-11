package team.creative.creativecore.client.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public class CreativeModelLoader implements IModelLoader<CreativeUnbakedModel> {
    
    @Override
    public CreativeUnbakedModel read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) throws JsonParseException {
        ResourceLocation block = null;
        if (jsonObject.has("block"))
            block = new ResourceLocation(jsonObject.get("block").getAsString());
        ResourceLocation item = null;
        if (jsonObject.has("item"))
            item = new ResourceLocation(jsonObject.get("item").getAsString());
        return new CreativeUnbakedModel(item, block);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }
}
