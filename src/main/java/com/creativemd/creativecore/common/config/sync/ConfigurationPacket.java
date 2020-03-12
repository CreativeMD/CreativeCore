package com.creativemd.creativecore.common.config.sync;

import com.creativemd.creativecore.common.config.gui.SubGuiClientSync;
import com.creativemd.creativecore.common.config.gui.SubGuiConfig;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigurationPacket extends CreativeCorePacket {
	
	public String[] path;
	public JsonObject json;
	public boolean ignoreRestart;
	
	public ConfigurationPacket(ICreativeConfigHolder holder, boolean ignoreRestart) {
		this.path = holder.path();
		this.json = holder.save(false, ignoreRestart, Side.SERVER);
		this.ignoreRestart = ignoreRestart;
	}
	
	public ConfigurationPacket() {
		
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		writeString(buf, String.join(".", path));
		writeJson(buf, json);
		buf.writeBoolean(ignoreRestart);
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		String text = readString(buf);
		path = text.isEmpty() ? new String[] {} : text.split(".");
		json = readJson(buf);
		ignoreRestart = buf.readBoolean();
	}
	
	@Override
	public void executeClient(EntityPlayer player) {
		ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(path);
		if (holder != null)
			holder.load(true, ignoreRestart, json, Side.SERVER);
		updateGui(player);
	}
	
	@Override
	public void executeServer(EntityPlayer player) {
		
	}
	
	public static void updateGui(EntityPlayer player) {
		if (player.openContainer instanceof ContainerSub) {
			if (((ContainerSub) player.openContainer).gui.isOpen(SubGuiConfig.class) || ((ContainerSub) player.openContainer).gui.isOpen(SubGuiClientSync.class))
				for (SubGui layer : ((ContainerSub) player.openContainer).gui.getLayers())
					if (layer instanceof SubGuiConfig) {
						((SubGuiConfig) layer).ROOT = new JsonObject();
						((SubGuiConfig) layer).loadHolder(((SubGuiConfig) layer).holder);
					} else if (layer instanceof SubGuiClientSync) {
						((SubGuiClientSync) layer).tree.reload();
						((SubGuiClientSync) layer).load(((SubGuiClientSync) layer).currentView);
					}
		}
	}
	
}
