package com.creativemd.creativecore;

import com.creativemd.creativecore.common.config.api.CreativeConfig;

public class CreativeCoreConfig {
	
	@CreativeConfig(name = "use-stencil")
	public boolean useStencil = true;
	
	@CreativeConfig
	public int[] keys = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 10, 12 };
	@CreativeConfig
	public String[] names = new String[] { "My", "text", "is", "awesome power!" };
	
}
