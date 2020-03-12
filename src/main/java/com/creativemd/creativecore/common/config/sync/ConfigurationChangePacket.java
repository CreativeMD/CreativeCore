package com.creativemd.creativecore.common.config.sync;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigurationChangePacket extends CreativeCorePacket {
	
	public String[] path;
	public JsonObject json;
	
	public ConfigurationChangePacket(ICreativeConfigHolder holder, JsonObject json) {
		this.path = holder.path();
		this.json = json;
	}
	
	public ConfigurationChangePacket() {
		
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		writeString(buf, String.join(".", path));
		writeJson(buf, json);
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		String text = readString(buf);
		path = text.isEmpty() ? new String[] {} : text.split(".");
		json = readJson(buf);
	}
	
	@Override
	public void executeClient(EntityPlayer player) {
		
	}
	
	@Override
	public void executeServer(EntityPlayer player) {
		if (player.canUseCommand(2, "")) {
			CreativeConfigRegistry.ROOT.followPath(path).load(false, true, json, Side.SERVER);
			CreativeCore.configHandler.save(Side.SERVER);
			CreativeCore.configHandler.syncAll();
		}
	}
	
}
