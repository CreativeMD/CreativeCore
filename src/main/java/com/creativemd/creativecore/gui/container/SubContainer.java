package com.creativemd.creativecore.gui.container;

import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.gui.ContainerControlUpdatePacket;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.gui.GuiNBTPacket;
import com.creativemd.creativecore.common.packet.gui.GuiUpdatePacket;
import com.creativemd.creativecore.event.CreativeCoreEventBus;
import com.creativemd.creativecore.gui.ContainerControl;
import com.creativemd.creativecore.gui.GuiControl;
import com.creativemd.creativecore.gui.event.ControlEvent;
import com.creativemd.creativecore.gui.mc.ContainerSub;
import com.creativemd.creativecore.gui.premade.SubContainerEmpty;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SubContainer extends ContainerParent {
	
	public EntityPlayer player;
	public ContainerSub container;
	
	private CreativeCoreEventBus eventBus;
	
	public SubContainer(EntityPlayer player) {
		this("container", player);
	}
	
	public SubContainer(String name, EntityPlayer player) {
		super(name);
		this.player = player;
		eventBus = new CreativeCoreEventBus(player.getEntityWorld().isRemote);
		addListener(this);
	}
	
	// ================LAYERS================
	
	public boolean isTopLayer() {
		return container.isTopLayer(this);
	}
	
	public int getLayerID() {
		return container.layers.indexOf(this);
	}
	
	public SubContainer createLayerFromPacket(World world, EntityPlayer player, NBTTagCompound nbt) {
		if (nbt.getBoolean("dialog")) {
			return new SubContainerEmpty(player);
		}
		return null;
	}
	
	public void closeLayer(NBTTagCompound nbt) {
		closeLayer(nbt, false);
	}
	
	public void closeLayer(NBTTagCompound nbt, boolean isPacket) {
		if (!isPacket) {
			PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, getLayerID(), true));
		}
		onClosed();
		onLayerClosed(nbt, this);
		container.layers.remove(this);
	}
	
	public void onLayerClosed(NBTTagCompound nbt, SubContainer container) {
	}
	
	public void openNewLayer(NBTTagCompound nbt) {
		openNewLayer(nbt, false);
	}
	
	public void openNewLayer(NBTTagCompound nbt, boolean isPacket) {
		SubContainer Subcontainer = createLayerFromPacket(player.world, player, nbt);
		Subcontainer.container = container;
		container.layers.add(Subcontainer);
		if (!isPacket) {
			PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, getLayerID(), false));
		}
	}
	
	// ================Interaction================
	
	@Override
	public boolean isInteractable() {
		return super.isInteractable() && isTopLayer();
	}
	
	// ================Internal Events================
	
	public boolean raiseEvent(ControlEvent event) {
		return !eventBus.raiseEvent(event);
	}
	
	public void addListener(Object listener) {
		eventBus.RegisterEventListener(listener);
	}
	
	public void removeListener(Object listener) {
		eventBus.removeEventListener(listener);
	}
	
	// ================CUSTOM EVENTS================
	
	@Override
	public void onClosed() {
		for (int i = 0; i < controls.size(); i++) {
			controls.get(i).onClosed();
		}
		eventBus.removeAllEventListeners();
	}
	
	@Override
	public void onOpened() {
		createControls();
		for (int i = 0; i < controls.size(); i++) {
			controls.get(i).parent = this;
			controls.get(i).onOpened();
			controls.get(i).setID(i);
		}
		// refreshControls();
		
		if (!player.getEntityWorld().isRemote) {
			NBTTagCompound nbt = new NBTTagCompound();
			writeOpeningNBT(nbt);
			if (!nbt.hasNoTags())
				PacketHandler.sendPacketToPlayer(new GuiUpdatePacket(nbt, true, getLayerID()), (EntityPlayerMP) player);
		}
	}
	
	// ================Helper================
	
	@Override
	public EntityPlayer getPlayer() {
		return player;
	}
	
	// ================Controls================
	
	public abstract void createControls();
	
	// ================Controls================
	/**
	 * If two players are looking into the same inventory (TileEntity). NOTE: Does
	 * only work for tileEntities because it uses the coordinates to identify a
	 * container
	 */
	@Override
	public void updateEqualContainers() {
		
	}
	
	// ================NETWORK================
	
	public void sendNBTToGui(NBTTagCompound nbt) {
		if (player instanceof EntityPlayerMP)
			PacketHandler.sendPacketToPlayer(new GuiNBTPacket(nbt), (EntityPlayerMP) player);
	}
	
	public void sendNBTUpdate(NBTTagCompound nbt) {
		sendNBTUpdate(null, nbt);
	}
	
	public void sendNBTUpdate(ContainerControl control, NBTTagCompound nbt) {
		String name = control != null ? control.name : null;
		if (player.getEntityWorld().isRemote)
			PacketHandler.sendPacketToServer(new ContainerControlUpdatePacket(getLayerID(), name, nbt));
		else
			PacketHandler.sendPacketToPlayer(new ContainerControlUpdatePacket(getLayerID(), name, nbt), (EntityPlayerMP) player);
	}
	
	/** Called once a player connects */
	public void writeOpeningNBT(NBTTagCompound nbt) {
	}
	
	@Override
	public void writeToNBTUpdate(NBTTagCompound nbt) {
	}
	
	// ================Client/Gui================
	
	@Override
	@SideOnly(Side.CLIENT)
	protected GuiControl createGuiControl() {
		return null;
	}
	
}
