package com.creativemd.creativecore.common.packet.gui;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.mc.ContainerSub;
import com.creativemd.creativecore.gui.mc.GuiContainerSub;
import com.creativemd.creativecore.gui.opener.CustomGuiHandler;
import com.creativemd.creativecore.gui.opener.GuiHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OpenGuiPacket extends CreativeCorePacket{
	
	public OpenGuiPacket()
	{
		
	}
	
	public String name;
	public NBTTagCompound nbt;
	
	public OpenGuiPacket(String name, NBTTagCompound nbt)
	{
		this.name = name;
		this.nbt = nbt;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		writeString(buf, name);
		writeNBT(buf, nbt);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		name = readString(buf);
		nbt = readNBT(buf);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		CustomGuiHandler handler = GuiHandler.getHandler(name);
		if(handler != null)
		{
			SubGui gui = handler.getGui(player, nbt);
			SubContainer container = handler.getContainer(player, nbt);
			if(gui != null && container != null)
				FMLCommonHandler.instance().showGuiScreen(new GuiContainerSub(player, gui, container));
		}
	}

	@Override
	public void executeServer(EntityPlayer player) {
		PacketHandler.sendPacketToPlayer(this, (EntityPlayerMP) player);
		CustomGuiHandler handler = GuiHandler.getHandler(name);
		if(handler != null)
		{
			SubContainer container = handler.getContainer(player, nbt);
			if(container != null)
				openContainerOnServer((EntityPlayerMP) player, new ContainerSub(player, container));
		}
	}

}
