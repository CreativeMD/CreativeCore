package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.gui.CustomGuiHandler;
import com.creativemd.creativecore.common.gui.GuiContainerSub;
import com.creativemd.creativecore.common.gui.GuiHandler;
import com.creativemd.creativecore.common.gui.SubGui;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

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
