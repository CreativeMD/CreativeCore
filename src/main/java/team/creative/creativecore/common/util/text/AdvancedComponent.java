package team.creative.creativecore.common.util.text;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public interface AdvancedComponent extends FormattedText {
    
    public int getWidth(Font font);
    
    public int getHeight(Font font);
    
    public boolean canSplit();
    
    public List<AdvancedComponent> split(int width, boolean force);
    
    public boolean isEmpty();
    
    public void render(PoseStack stack, Font font, int defaultColor);
    
    public static class Serializer extends Component.Serializer {
        
        @Override
        public JsonElement serialize(Component component, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonobject = new JsonObject();
            
            if (component instanceof AdvancedComponent) {
                if (!component.getSiblings().isEmpty()) {
                    JsonArray jsonarray = new JsonArray();
                    
                    for (Component child : component.getSiblings()) {
                        jsonarray.add(this.serialize(child, child.getClass(), context));
                    }
                    
                    jsonobject.add("extra", jsonarray);
                }
                if (component instanceof ItemStackComponent stackComponent)
                    jsonobject.addProperty("stack", stackComponent.stack.save(new CompoundTag()).toString());
                else if (component instanceof LinebreakComponent)
                    jsonobject.addProperty("linebreak", true);
                else
                    throw new IllegalArgumentException("Don't know how to serialize " + component + " as an AdvancedComponent");
            } else
                return super.serialize(component, typeOfSrc, context);
            
            return jsonobject;
        }
        
        @Override
        public MutableComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject())
                return super.deserialize(json, typeOfT, context);
            
            JsonObject jsonobject = json.getAsJsonObject();
            if (jsonobject.has("stack"))
                try {
                    return new TextComponent("").append(new ItemStackComponent(ItemStack.of(TagParser.parseTag(jsonobject.get("stack").getAsString()))));
                } catch (CommandSyntaxException e) {
                    throw new JsonParseException(e);
                }
            else if (jsonobject.has("linebreak"))
                return new TextComponent("").append(new LinebreakComponent());
            
            return super.deserialize(json, typeOfT, context);
        }
        
    }
    
}
