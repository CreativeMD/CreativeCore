package com.creativemd.creativecore.common.container.slot;

import java.util.HashMap;
import java.util.Map.Entry;

import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.packet.ContainerControlUpdatePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ContainerControl {
	
	public SubContainer parent;
	
	private int id = -1;
	
	public void setID(int id)
	{
		if(this.id != -1)
			this.id = id;
	}
	
	public void sendUpdate()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			PacketHandler.sendPacketToPlayer(new ContainerControlUpdatePacket(id, nbt), (EntityPlayerMP) parent.player);
		else
			PacketHandler.sendPacketToServer(new ContainerControlUpdatePacket(id, nbt));
	}
	
	@SideOnly(Side.CLIENT)
	public abstract GuiControl getClientControl();
	
	//private SyncControl sync = SyncControl.NONE;
	
	/*public void onControlChanged()
	{
		boolean shouldSync = sync == SyncControl.BOTH;
		if(FMLCommonHandler.instance().getEffectiveSide().isClient() && sync == SyncControl.CLIENTTOSERVER)
			shouldSync = true;
		if(FMLCommonHandler.instance().getEffectiveSide().isServer() && sync == SyncControl.SERVERTOCLIENT)
			shouldSync = true;
		
		if(shouldSync)
		{
			
		}
	}*/
	
	public abstract void writeToNBT(NBTTagCompound nbt);
	
	public abstract void readFromNBT(NBTTagCompound nbt);
	
	/*public boolean doesControlSupportSync()
	{
		return containsClass(this.getClass());
	}*/
	
//	public static enum SyncControl {
//		/**No synchronization**/
//		NONE,
//		/**I don't know if anyone will use it.
//		 * Only the client is allowed to send updates to the server,
//		 * any client update won't be send back from the server to the viewers
//		 **/
//		CLIENTTOSERVER,
//		/**Only the server is allowed to send updates to the client,
//		 * the client isn't able to do anything
//		 **/
//		SERVERTOCLIENT,
//		/**synchronization**/
//		BOTH
//	}
	
	private static HashMap<String, Class<? extends ContainerControl>> controls = new HashMap<String, Class<? extends ContainerControl>>();
	
	public static void registerControl(String id, Class<? extends ContainerControl> ControlClass)
	{
		if(containsID(id))
			throw new IllegalArgumentException("ID=" + id + " is already taken!");
		if(containsClass(ControlClass))
			throw new IllegalArgumentException("Class=" + ControlClass.getName() + " is already registered!");
		
		controls.put(id, ControlClass);
	}
	
	public static Class<? extends ContainerControl> getClassByID(String id)
	{
		return controls.get(id);
	}
	
	public static String getIDbyCkass(Class<? extends ContainerControl> ControlClass)
	{
		for (Entry<String, Class<? extends ContainerControl>> entry : controls.entrySet()) {
	        if (ControlClass == entry.getValue()) {
	            return entry.getKey();
	        }
	    }
		return "";
	}
	
	public static boolean containsClass(Class<? extends ContainerControl> ControlClass)
	{
		return controls.containsValue(ControlClass);
	}
	
	public static boolean containsID(String id)
	{
		return controls.containsKey(id);
	}
	
}
