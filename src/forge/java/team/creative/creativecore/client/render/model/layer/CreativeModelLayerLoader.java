package team.creative.creativecore.client.render.model.layer;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.client.render.model.layer.CreativeUnbakedItemLayer.CreativeUnbakedItemLayerWrapper;
import team.creative.creativecore.common.util.registry.NamedTypeRegistry;

public class CreativeModelLayerLoader implements IGeometryLoader<CreativeUnbakedLayerModel> {
    
    public static final NamedTypeRegistry<CreativeUnbakedItemLayer> REGISTRY = new NamedTypeRegistry<CreativeUnbakedItemLayer>().addConstructorPattern(JsonObject.class);
    
    @Override
    public CreativeUnbakedLayerModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        List<CreativeUnbakedItemLayer> layers = new ArrayList<>();
        if (jsonObject.has("layers") && jsonObject.get("layers").isJsonArray()) {
            for (JsonElement element : jsonObject.getAsJsonArray("layers")) {
                if (!element.isJsonObject()) {
                    CreativeCore.LOGGER.error("Invalid entry {} in model definition {}", element.getAsString(), jsonObject);
                    continue;
                }
                var loaderElement = element.getAsJsonObject().get("loader");
                layers.add(REGISTRY.createSafe(CreativeUnbakedItemLayerWrapper.class, loaderElement != null ? loaderElement.getAsString() : "", element.getAsJsonObject()));
            }
        } else
            CreativeCore.LOGGER.error("Invalid model definition {}", jsonObject);
        return new CreativeUnbakedLayerModel(layers);
    }
    
}
