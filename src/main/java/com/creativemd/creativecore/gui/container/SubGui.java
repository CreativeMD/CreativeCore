package com.creativemd.creativecore.gui.container;

import java.util.ArrayList;

import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.gui.GuiUpdatePacket;
import com.creativemd.creativecore.event.CreativeCoreEventBus;
import com.creativemd.creativecore.gui.ContainerControl;
import com.creativemd.creativecore.gui.CoreControl;
import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.event.ControlEvent;
import com.creativemd.creativecore.gui.event.gui.GuiControlEvent;
import com.creativemd.creativecore.gui.event.gui.GuiToolTipEvent;
import com.creativemd.creativecore.gui.mc.GuiContainerSub;
import com.creativemd.creativecore.gui.premade.SubGuiDialog;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class SubGui extends GuiParent {
	
	public SubContainer container;
	
	public GuiContainerSub gui;
	
	private CreativeCoreEventBus eventBus;
	
	public SubGui() {
		this(176, 166);
	}
	
	public SubGui(int width, int height) {
		this("gui", width, height);
	}
	
	public SubGui(String name, int width, int height) {
		super(name, 0, 0, width, height);
		eventBus = new CreativeCoreEventBus(true);
		eventBus.RegisterEventListener(this);
	}
	
	//================Layers================
	
	public boolean isTopLayer()
	{
		return gui.isTopLayer(this);
	}
	
	public int getLayerID()
	{
		return gui.getLayers().indexOf(this);
	}
    
    public void openNewLayer(NBTTagCompound nbt)
    {
    	openNewLayer(nbt, false);
    }
    
    public void openNewLayer(NBTTagCompound nbt, boolean isPacket)
    {
    	gui.addLayer(createLayer(mc.world, mc.player, nbt));
    	if(!isPacket)
    		PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, getLayerID(), false));
    }
    
    public void closeLayer(NBTTagCompound nbt)
    {
    	closeLayer(nbt, false);
    }
    
    public void closeLayer(NBTTagCompound nbt, boolean isPacket)
    {
    	onClosed();
    	if(!isPacket)
    		PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, getLayerID(), true));
    	gui.removeLayer(this);
    	if(gui.hasTopLayer())
    		gui.getTopLayer().onLayerClosed(this, nbt);
    }
    
    public void onLayerClosed(SubGui gui, NBTTagCompound nbt)
    {
    	if(nbt.getBoolean("dialog"))
    	{
    		String[] buttons = new String[nbt.getInteger("count")];
    		for (int i = 0; i < buttons.length; i++) {
				buttons[i] = nbt.getString("b" + i);
			}
    		onDialogClosed(nbt.getString("text"), buttons, nbt.getString("clicked"));
    	}
    }
    
    public void closeGui()
    {
    	NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("exit", true);
		closeLayer(nbt);
		if(gui.getLayers().size() == 0)
			mc.player.closeScreen();
    }
    
    public SubGui createLayer(World world, EntityPlayer player, NBTTagCompound nbt)
    {
    	SubGui layer = createLayerFromPacket(world, player, nbt);
    	layer.container = container.createLayerFromPacket(world, player, nbt);
    	layer.gui = gui; 
    	layer.onOpened();
    	return layer;
    }
    
    public SubGui createLayerFromPacket(World world, EntityPlayer player, NBTTagCompound nbt)
    {
    	if(nbt.getBoolean("dialog"))
    	{
    		String[] buttons = new String[nbt.getInteger("count")];
    		for (int i = 0; i < buttons.length; i++) {
				buttons[i] = nbt.getString("b" + i);
			}
    		return new SubGuiDialog(nbt.getString("text"), buttons);
    	}
    	return null;
    }
    
    //================DIALOGS================
    
    public void openYesNoDialog(String text)
    {
    	openButtonDialogDialog(text, "Yes", "No");
    }
    
    public void openButtonDialogDialog(String text, String... buttons)
    {
    	NBTTagCompound nbt = new NBTTagCompound();
    	nbt.setBoolean("dialog", true);
    	nbt.setString("text", text);
    	nbt.setInteger("count", buttons.length);
    	for (int i = 0; i < buttons.length; i++) {
			nbt.setString("b" + i, buttons[i]);
		}
    	openNewLayer(nbt);
    }
    
    public void openSaveDialog(String text)
    {
    	openButtonDialogDialog(text, "Yes", "No", "Cancel");
    }
    
    public void onDialogClosed(String text, String[] buttons, String clicked)
    {
    	
    }
	
	//================Interaction================
	
	@Override
	public boolean isInteractable()
	{
		return super.isInteractable() && isTopLayer();
	}
	
	public boolean closeGuiUsingEscape()
	{
		return true;
	}
	
	@Override
	public boolean onKeyPressed(char character, int key)
	{
		if (key == 1 || key == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
			if(closeGuiUsingEscape())
				closeGui();
            return true;
        }
		return super.onKeyPressed(character, key);
	}
	
	//================NETWORK================
	
	/*public void readFromOpeningNBT(NBTTagCompound nbt){}*/
	
	public void receiveContainerPacket(NBTTagCompound nbt) {}
	
	public void sendPacketToServer(NBTTagCompound nbt)
	{
		PacketHandler.sendPacketToServer(new GuiUpdatePacket(nbt, false, getLayerID()));
	}
	
	//================Helper================
	
	@Override
	public EntityPlayer getPlayer()
	{
		return container.player;
	}
	
	//================Controls================
	
	public abstract void createControls();
	
	//================Rendering================
	
	@Override
	protected void renderBackground(GuiRenderHelper helper, Style style)
	{
		style.getBorder(this).renderStyle(helper, width, height);
		GlStateManager.translate(borderWidth, borderWidth, 0);
		style.getFace(this).renderStyle(helper, width-borderWidth*2, height-borderWidth*2);
	}
	
	@Override
	public boolean isMouseOver()
	{
		if(parent != null)
			return super.isMouseOver();
		return isTopLayer();
	}
	
	//================CUSTOM EVENTS================
	
	public void onTick() {}
	
	@Override
	public void onClosed()
	{
		super.onClosed();
		eventBus.removeAllEventListeners();
	}
	
	public void addContainerControls()
	{
		for (int i = 0; i < container.controls.size(); i++) {
			container.controls.get(i).onOpened();
			controls.add(container.controls.get(i).getGuiControl());
		}
	}
	
	@Override
	public void onOpened()
    {
    	createControls();
    	for (int i = 0; i < controls.size(); i++) {
    		controls.get(i).parent = this;
    		controls.get(i).onOpened();
    	}
    	
    	if(container != null)
    	{
    		addContainerControls();
    	}
		refreshControls();
    }
	
	//================Internal Events================
	
	public boolean raiseEvent(ControlEvent event)
	{
		return !eventBus.raiseEvent(event);
	}
	
	public void addListener(Object listener)
	{
		eventBus.RegisterEventListener(listener);
	}
	
	public void removeListener(Object listener)
	{
		eventBus.removeEventListener(listener);
	}

}
