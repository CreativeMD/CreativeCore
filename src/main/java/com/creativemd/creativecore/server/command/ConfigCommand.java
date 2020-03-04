package com.creativemd.creativecore.server.command;

import com.creativemd.creativecore.common.gui.opener.GuiHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class ConfigCommand extends CommandBase {
	
	@Override
	public String getName() {
		return "cmdconfig";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "opens config gui";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		GuiHandler.openGui("config", new NBTTagCompound());
	}
	
}
