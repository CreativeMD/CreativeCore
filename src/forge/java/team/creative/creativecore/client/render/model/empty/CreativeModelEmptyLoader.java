package team.creative.creativecore.client.render.model.empty;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class CreativeModelEmptyLoader implements IGeometryLoader<CreativeUnbakedEmptyModel> {
    
    @Override
    public CreativeUnbakedEmptyModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        var particle = jsonObject.get("particle");
        return new CreativeUnbakedEmptyModel(particle != null ? particle.getAsString() : MissingTextureAtlasSprite.getLocation().toString());
    }
    
}
