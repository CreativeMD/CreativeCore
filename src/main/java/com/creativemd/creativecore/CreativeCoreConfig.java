package com.creativemd.creativecore;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;

public class CreativeCoreConfig {
	
	@CreativeConfig(name = "use-stencil", type = ConfigSynchronization.CLIENT)
	public boolean useStencil = true;
	
	@CreativeConfig(name = "use-optifine-compat", type = ConfigSynchronization.CLIENT)
	public boolean useOptifineCompat = true;
	
}
