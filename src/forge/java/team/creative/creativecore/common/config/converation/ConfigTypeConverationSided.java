package team.creative.creativecore.common.config.converation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.converation.ConfigTypeConveration.SimpleConfigTypeConveration;
import team.creative.creativecore.common.config.gui.GuiButtonKeyConfig;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.premade.KeyConfig;
import team.creative.creativecore.common.gui.GuiParent;

public class ConfigTypeConverationSided {
    
    public static void registerSide() {
        ConfigTypeConveration.registerType(KeyConfig.class, new SimpleConfigTypeConveration<KeyConfig>() {
            
            @Override
            public KeyConfig set(ConfigKey key, KeyConfig value) {
                return value;
            }
            
            @Override
            public JsonElement writeElement(KeyConfig value, ConfigKey key, Side side) {
                JsonObject object = new JsonObject();
                object.addProperty("key", value.keyCode);
                object.addProperty("scan", value.scanCode);
                object.addProperty("modifier", value.modifier);
                return object;
            }
            
            @Override
            public KeyConfig readElement(ConfigKey key, KeyConfig defaultValue, Side side, JsonElement element) {
                if (element instanceof JsonObject object)
                    return new KeyConfig(object.get("key").getAsInt(), object.get("scan").getAsInt(), object.get("modifier").getAsInt());
                return KeyConfig.UNBOUND;
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            protected KeyConfig saveValue(GuiParent parent, ConfigKey key) {
                return parent.get("key", GuiButtonKeyConfig.class).getValue();
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            public void loadValue(KeyConfig value, GuiParent parent) {
                parent.get("key", GuiButtonKeyConfig.class).setValue(value);
            }
            
            @Override
            @Environment(EnvType.CLIENT)
            public void createControls(GuiParent parent, ConfigKey key) {
                parent.add(new GuiButtonKeyConfig(parent, "key", KeyConfig.UNBOUND));
            }
        });
    }
}
