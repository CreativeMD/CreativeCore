package team.creative.creativecore.common.config.sync;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.network.CreativePacket;

public class ConfigurationChangePacket extends CreativePacket {
	
	public String[] path;
	public JsonObject json;
	
	public ConfigurationChangePacket(ICreativeConfigHolder holder, JsonObject json) {
		this.path = holder.path();
		this.json = json;
	}
	
	public ConfigurationChangePacket() {
		
	}
	
	@Override
	public void executeClient(PlayerEntity player) {
		
	}
	
	@Override
	public void executeServer(PlayerEntity player) {
		if (player.hasPermissionLevel(2)) {
			CreativeConfigRegistry.ROOT.followPath(path).load(false, true, json, Dist.DEDICATED_SERVER);
			CreativeCore.CONFIG_HANDLER.save(Dist.DEDICATED_SERVER);
			CreativeCore.CONFIG_HANDLER.syncAll();
		} else
			CreativeCore.CONFIG_HANDLER.syncAll((ServerPlayerEntity) player);
	}
	
}
