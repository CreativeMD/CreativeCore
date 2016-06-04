package com.creativemd.creativecore.command;

import com.creativemd.creativecore.gui.opener.GuiHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class GuiCommand extends CommandBase{

	@Override
	public String getCommandName() {
		return "creative-gui";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "opens a test gui";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		GuiHandler.openGui("test-gui", new NBTTagCompound());
	}
	
}
