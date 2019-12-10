package team.creative.creativecore;

import team.creative.creativecore.common.config.CreativeConfig;
import team.creative.creativecore.common.config.CreativeConfigBase;

public class CreativeCoreConfig extends CreativeConfigBase {
	
	@CreativeConfig
	public boolean fixInventoryTab = true;
	
	@Override
	public void configured() {
		
	}
	
}
