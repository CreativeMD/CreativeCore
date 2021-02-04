package team.creative.creativecore.common.config.gui;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarButton;
import com.creativemd.creativecore.common.gui.premade.SubContainerEmpty;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.utils.mc.MaterialUtils;
import com.creativemd.creativecore.common.utils.stack.InfoBlock;
import com.creativemd.creativecore.common.utils.stack.InfoContainOre;
import com.creativemd.creativecore.common.utils.stack.InfoFuel;
import com.creativemd.creativecore.common.utils.stack.InfoItem;
import com.creativemd.creativecore.common.utils.stack.InfoItemStack;
import com.creativemd.creativecore.common.utils.stack.InfoMaterial;
import com.creativemd.creativecore.common.utils.stack.InfoName;
import com.creativemd.creativecore.common.utils.stack.InfoOre;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;

public class GuiInfoStackButton extends GuiAvatarButton {
	
	private CreativeIngredient info;
	
	public GuiInfoStackButton(String name, int x, int y, int width, int height, CreativeIngredient info) {
		super(name, getLabelText(info), x, y, width, height, new AvatarItemStack(info.getItemStack()));
		this.info = info;
	}
	
	public void set(CreativeIngredient info) {
		this.info = info;
		this.avatar = new AvatarItemStack(info.getItemStack());
		this.caption = getLabelText(info);
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	public static String getLabelText(CreativeIngredient value) {
		if (value == null)
			return "";
		else {
			if (value instanceof InfoBlock)
				return "Block: " + ChatFormatting.YELLOW + Block.REGISTRY.getNameForObject(((InfoBlock) value).block).toString();
			else if (value instanceof InfoItem)
				return "Item: " + ChatFormatting.YELLOW + Item.REGISTRY.getNameForObject(((InfoItem) value).item).toString();
			else if (value instanceof InfoItemStack)
				return "ItemStack";
			else if (value instanceof InfoMaterial) {
				return "Material: " + ChatFormatting.YELLOW + MaterialUtils.getNameOrDefault(((InfoMaterial) value).material, "unkown");
			} else if (value instanceof InfoOre)
				return "Ore: " + ChatFormatting.YELLOW + ((InfoOre) value).ore;
			else if (value instanceof InfoContainOre)
				return "Ore (Contains): " + ChatFormatting.YELLOW + ((InfoContainOre) value).ore;
			else if (value instanceof InfoName)
				return "Name (Contains): " + ChatFormatting.YELLOW + ((InfoName) value).name;
			else if (value instanceof InfoFuel)
				return "All fuels";
			else
				return "no description given.";
		}
	}
	
	@Override
	public void onClicked(int x, int y, int button) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("dialog", true);
		SubGui gui = new FullItemDialogGuiLayer(this, false);
		gui.container = new SubContainerEmpty(getPlayer());
		gui.gui = getGui().gui;
		getGui().gui.addLayer(gui);
		PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, gui.gui.getLayers().size() - 1, false));
		gui.onOpened();
	}
	
	public CreativeIngredient get() {
		return info;
	}
	
}
