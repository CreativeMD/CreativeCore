package team.creative.creativecore.client.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public class CreativeModelLoader implements IModelLoader<CreativeUnbakedModel> {
    
    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {}
    
    @Override
    public CreativeUnbakedModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return CreativeUnbakedModel.INSTANCE;
    }
    
}
