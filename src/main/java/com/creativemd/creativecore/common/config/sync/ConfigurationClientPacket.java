package com.creativemd.creativecore.common.config.sync;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.event.ConfigEventHandler;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.type.CheckTree;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigurationClientPacket extends CreativeCorePacket {
	
	public String[] path;
	public List<String> enabled;
	
	public ConfigurationClientPacket(ICreativeConfigHolder holder, CheckTree<ConfigKey> tree) {
		this.path = holder.path();
		this.enabled = buildClientFieldList(tree.root, new ArrayList<>());
	}
	
	public ConfigurationClientPacket(ICreativeConfigHolder holder) {
		this.path = holder.path();
		this.enabled = ConfigEventHandler.loadClientFieldList(holder);
	}
	
	public List<String> buildClientFieldList(CheckTree<ConfigKey>.CheckTreeEntry entry, List<String> list) {
		if (entry.isEnabled() && entry.content != null) {
			String path;
			
			if (entry.parent != null && entry.parent.content != null)
				path = String.join(".", ((ICreativeConfigHolder) entry.parent.content.get()).path()) + ".";
			else
				path = "";
			
			list.add(path + entry.content.name);
			return list;
		}
		
		if (entry.children != null)
			for (CheckTree<ConfigKey>.CheckTreeEntry child : entry.children)
				buildClientFieldList(child, list);
			
		return list;
	}
	
	public ConfigurationClientPacket() {
		
	}
	
	@Override
	public void writeBytes(ByteBuf buf) {
		writeString(buf, String.join(".", path));
		buf.writeInt(enabled.size());
		for (int i = 0; i < enabled.size(); i++)
			writeString(buf, enabled.get(i));
	}
	
	@Override
	public void readBytes(ByteBuf buf) {
		String text = readString(buf);
		path = text.isEmpty() ? new String[] {} : text.split(".");
		int size = buf.readInt();
		enabled = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			enabled.add(readString(buf));
	}
	
	public ICreativeConfigHolder execute() {
		ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(path);
		if (holder != null)
			ConfigEventHandler.saveClientFieldList(holder, enabled);
		return holder;
	}
	
	@Override
	public void executeClient(EntityPlayer player) {
		execute();
		ConfigurationPacket.updateGui(player);
	}
	
	@Override
	public void executeServer(EntityPlayer player) {
		PacketHandler.sendPacketToAllPlayers(new ConfigurationClientPacket(execute()));
		CreativeCore.configHandler.saveClientFields();
		CreativeCore.configHandler.save(Side.SERVER);
	}
	
}
