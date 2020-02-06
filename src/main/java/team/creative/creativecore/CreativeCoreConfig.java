package team.creative.creativecore;

import team.creative.creativecore.common.config.CreativeConfig;
import team.creative.creativecore.common.config.ICreativeConfig;

public class CreativeCoreConfig implements ICreativeConfig {
	
	@CreativeConfig
	public boolean fixInventoryTab = true;
	
	@Override
	public void configured() {
		
	}
	
}
