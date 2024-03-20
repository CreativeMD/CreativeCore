package team.creative.creativecore.client;

import java.util.HashMap;
import java.util.Map;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screens.Screen;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.ConfigGuiLayer;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.gui.integration.GuiScreenIntegration;

public class ModMenuImpl implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return getConfig(CreativeCore.MODID);
    }
    
    private ConfigScreenFactory<Screen> getConfig(String modid) {
        return (screen) -> {
            ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(modid);
            if (holder != null && !holder.isEmpty(Side.CLIENT))
                return new GuiScreenIntegration(new ConfigGuiLayer(holder, Side.CLIENT)) {
                    
                    @Override
                    public void onClose() {
                        this.minecraft.setScreen(screen);
                    }
                    
                };
            return null;
        };
        
    }
    
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> map = new HashMap<>();
        for (String modid : CreativeCoreClient.getModConfigs()) {
            ConfigScreenFactory<Screen> screen = getConfig(modid);
            if (screen != null)
                map.put(modid, screen);
        }
        return map;
    }
}
