package team.creative.creativecore.common.config.sync;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.network.CreativePacket;

public class ConfigurationPacket extends CreativePacket {
	
	public String[] path;
	public JsonObject json;
	
	public ConfigurationPacket(ICreativeConfigHolder holder) {
		this.path = holder.path();
		this.json = holder.save(false, Dist.DEDICATED_SERVER);
	}
	
	public ConfigurationPacket() {
		
	}
	
	@Override
	public void executeClient(PlayerEntity player) {
		ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(path);
		if (holder != null)
			holder.load(true, json, Dist.DEDICATED_SERVER);
	}
	
	@Override
	public void executeServer(PlayerEntity player) {
		
	}
	
}
