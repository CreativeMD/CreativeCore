package com.creativemd.creativecore.common.utils.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerUtils {
	
	@SideOnly(Side.CLIENT)
	private static boolean isAdventureClient(EntityPlayer player) {
		return Minecraft.getMinecraft().playerController.getCurrentGameType() == GameType.ADVENTURE;
	}
	
	public static boolean isAdventure(EntityPlayer player) {
		if (player.world.isRemote)
			return isAdventureClient(player);
		return ((EntityPlayerMP) player).interactionManager.getGameType() == GameType.ADVENTURE;
	}
	
	@SideOnly(Side.CLIENT)
	private static GameType getGameTypeClient(EntityPlayer player) {
		return Minecraft.getMinecraft().playerController.getCurrentGameType();
	}
	
	public static GameType getGameType(EntityPlayer player) {
		if (player.world.isRemote)
			return getGameTypeClient(player);
		return ((EntityPlayerMP) player).interactionManager.getGameType();
	}
}
