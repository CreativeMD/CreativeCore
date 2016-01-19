package com.n247s.N2ConfigApi.api.core;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import scala.actors.threadpool.Arrays;

import com.n247s.N2ConfigApi.api.core.ConfigHandler.ProxySide;

/**
 * @author N247S
 * An ingame ConfigFile Manager
 */

public class DefaultConfigFile extends ConfigFile
{
	private static final String sectionOutLine = "-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/";
	private static String[] fileDescription;


	public DefaultConfigFile(String fileName, String configName)
	{
		super(fileName, ProxySide.Common);

		this.setCustomSectionStarter(sectionOutLine);
		this.setCustomSectionHeadEnder(sectionOutLine);
		this.setCustomSectionEnder(sectionOutLine);
		
		
		this.fileDescription = new String[]
			{
				configName +  " Config File",
				" ",
				"warning, its recomended to backup a custom WORKING config file before editing.",
				"any misstakes may cause an resset!"
			};
		this.setDescription(fileDescription);
	}

	@Override
	public void generateFile() {}

	@Override
	public boolean getPermission(EntityPlayerMP player)
	{
		return(MinecraftServer.getServer() != null &&
				Arrays.asList(MinecraftServer.getServer().getConfigurationManager().func_152606_n()).contains(player.getGameProfile().toString()) ||
				MinecraftServer.getServer().getConfigurationManager().getServerInstance().getServerOwner().equalsIgnoreCase(player.getGameProfile().toString())) ||
				Minecraft.getMinecraft().isSingleplayer();
	}
}
