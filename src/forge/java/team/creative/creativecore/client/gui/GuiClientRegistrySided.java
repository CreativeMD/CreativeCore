package team.creative.creativecore.client.gui;

import team.creative.creativecore.client.config.gui.GuiClientButtonKeyConfig;
import team.creative.creativecore.client.gui.registry.GuiClientRegistry;
import team.creative.creativecore.common.config.gui.GuiButtonKeyConfig;

public class GuiClientRegistrySided {
    
    public static void register() {
        GuiClientRegistry.register(GuiButtonKeyConfig.class, GuiClientButtonKeyConfig::new);
    }
    
}
