package com.creativemd.creativecore.common.utils.stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.creativemd.creativecore.common.config.gui.SubGuiFullItemDialog;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.gui.controls.gui.custom.GuiStackSelectorAll;
import com.creativemd.creativecore.common.gui.controls.gui.custom.GuiStackSelectorAll.SearchSelector;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@SideOnly(Side.CLIENT)
public abstract class GuiInfoHandler {
	
	private static LinkedHashMap<String, GuiInfoHandler> handlers = new LinkedHashMap<>();
	
	public static void registerGuiInfoHandler(String name, GuiInfoHandler handler) {
		handler.name = name;
		handlers.put(name, handler);
	}
	
	public static Set<String> getNames() {
		return handlers.keySet();
	}
	
	public static GuiInfoHandler getHandler(InfoStack info) {
		if (info != null) {
			for (Iterator<GuiInfoHandler> iterator = handlers.values().iterator(); iterator.hasNext();) {
				GuiInfoHandler handler = iterator.next();
				if (handler.canHandle(info))
					return handler;
			}
		}
		return GuiInfoHandler.defaultHandler;
	}
	
	public static GuiInfoHandler getHandler(String name) {
		GuiInfoHandler handler = handlers.get(name);
		if (handler == null)
			return defaultHandler;
		return handler;
	}
	
	public static GuiInfoHandler defaultHandler;
	
	static {
		defaultHandler = new GuiInfoHandler() {
			
			@Override
			public void createControls(SubGuiFullItemDialog gui, InfoStack info) {
				GuiStackSelectorAll selector = new GuiStackSelectorAll("inv", 0, 30, 122, null, new GuiStackSelectorAll.CreativeCollector(new GuiStackSelectorAll.SearchSelector()));
				gui.controls.add(selector);
				gui.controls.add(new GuiTextfield("search", "", 0, 57, 144, 14));
				
				gui.controls.add(new GuiLabel("guilabel1", 0, 80));
				gui.controls.add(new GuiLabel("guilabel2", 0, 90));
				
				GuiStateButton damage = new GuiStateButton("damage", 0, 0, 106, 70, 14, "Damage: Off", "Damage: On");
				gui.controls.add(damage);
				GuiStateButton nbt = new GuiStateButton("nbt", 0, 80, 106, 60, 14, "NBT: Off", "NBT: On");
				gui.controls.add(nbt);
				
				if (info instanceof InfoBlock || info instanceof InfoItem || info instanceof InfoItemStack) {
					selector.setSelectedForce(info.getItemStack().copy());
					if (info instanceof InfoItemStack) {
						damage.nextState();
						if (((InfoItemStack) info).needNBT)
							nbt.nextState();
					}
				}
				
				onChanged(gui, new GuiControlChangedEvent(selector));
			}
			
			@Override
			public boolean canHandle(InfoStack info) {
				return info instanceof InfoBlock || info instanceof InfoItem || info instanceof InfoItemStack;
			}
			
			@Override
			public InfoStack parseInfo(SubGuiFullItemDialog gui, int stackSize) {
				ItemStack stack = ((GuiStackSelectorAll) gui.get("inv")).getSelected();
				if (stack != null) {
					boolean damage = ((GuiStateButton) gui.get("damage")).getState() == 1;
					boolean nbt = ((GuiStateButton) gui.get("nbt")).getState() == 1;
					if (damage) {
						return new InfoItemStack(stack.copy(), nbt, stackSize);
					} else {
						if (!(Block.getBlockFromItem(stack.getItem()) instanceof BlockAir))
							return new InfoBlock(Block.getBlockFromItem(stack.getItem()), stackSize);
						else
							return new InfoItem(stack.getItem(), stackSize);
					}
				}
				return null;
			}
			
			@Override
			public void onChanged(SubGuiFullItemDialog gui, GuiControlChangedEvent event) {
				if (event.source.is("search")) {
					GuiStackSelectorAll inv = (GuiStackSelectorAll) gui.get("inv");
					((SearchSelector) inv.collector.selector).search = ((GuiTextfield) event.source).text.toLowerCase();
					inv.updateCollectedStacks();
					inv.closeBox();
				} else if (event.source.is("inv")) {
					GuiStackSelectorAll selector = (GuiStackSelectorAll) gui.get("inv");
					if (selector != null) {
						ItemStack stack = selector.getSelected();
						if (!stack.isEmpty()) {
							
							((GuiLabel) gui.get("guilabel1")).setCaption("damage: " + stack.getItemDamage());
							((GuiLabel) gui.get("guilabel2")).setCaption("nbt: " + (stack.hasTagCompound() ? stack.getTagCompound().toString() : "null"));
						} else {
							((GuiLabel) gui.get("guilabel1")).setCaption("");
							((GuiLabel) gui.get("guilabel2")).setCaption("");
						}
					}
				}
			}
		};
		registerGuiInfoHandler("Default", defaultHandler);
		
		registerGuiInfoHandler("Ore", new GuiInfoHandler() {
			
			@Override
			public InfoStack parseInfo(SubGuiFullItemDialog gui, int stackSize) {
				String ore = ((GuiComboBox) gui.get("ore")).getCaption();
				if (!ore.equals(""))
					return new InfoOre(ore, stackSize);
				return null;
			}
			
			@Override
			public void createControls(SubGuiFullItemDialog gui, InfoStack info) {
				ArrayList<String> ores = new ArrayList<String>(Arrays.asList(OreDictionary.getOreNames()));
				GuiComboBox ore = new GuiComboBox("ore", 0, 30, 144, ores);
				gui.controls.add(ore);
				gui.controls.add(new GuiTextfield("search", "", 0, 57, 144, 14));
				
				if (info instanceof InfoOre)
					ore.setCaption(((InfoOre) info).ore);
			}
			
			@Override
			public boolean canHandle(InfoStack info) {
				return info instanceof InfoOre;
			}
			
			@Override
			public void onChanged(SubGuiFullItemDialog gui, GuiControlChangedEvent event) {
				if (event.source.is("search")) {
					String search = ((GuiTextfield) event.source).text;
					String[] oreNames = OreDictionary.getOreNames();
					ArrayList<String> ores = new ArrayList<String>();
					for (int i = 0; i < oreNames.length; i++) {
						if (oreNames[i].toLowerCase().contains(search.toLowerCase()))
							ores.add(oreNames[i]);
					}
					GuiComboBox comboBox = (GuiComboBox) gui.get("ore");
					if (comboBox != null) {
						comboBox.lines = ores;
						if (!ores.contains(comboBox.getCaption())) {
							if (ores.size() > 0)
								comboBox.setCaption(ores.get(0));
							else
								comboBox.setCaption("");
						}
					}
				}
			}
		});
		
		registerGuiInfoHandler("Material", new GuiInfoHandler() {
			
			@Override
			public InfoStack parseInfo(SubGuiFullItemDialog gui, int stackSize) {
				ItemStack blockStack = ((GuiStackSelectorAll) gui.get("inv")).getSelected();
				if (blockStack != null) {
					Block block = Block.getBlockFromItem(blockStack.getItem());
					if (!(block instanceof BlockAir))
						return new InfoMaterial(block.getMaterial(null), stackSize);
				}
				return null;
			}
			
			@Override
			public void createControls(SubGuiFullItemDialog gui, InfoStack info) {
				GuiStackSelectorAll selector = new GuiStackSelectorAll("inv", 0, 30, 122, null, new GuiStackSelectorAll.CreativeCollector(new GuiStackSelectorAll.GuiBlockSelector()));
				gui.controls.add(selector);
				if (info instanceof InfoMaterial)
					selector.setSelectedForce(info.getItemStack());
			}
			
			@Override
			public boolean canHandle(InfoStack info) {
				return info instanceof InfoMaterial;
			}
		});
		
		registerGuiInfoHandler("Fuel", new GuiInfoHandler() {
			
			@Override
			public InfoStack parseInfo(SubGuiFullItemDialog gui, int stackSize) {
				return new InfoFuel(stackSize);
			}
			
			@Override
			public void createControls(SubGuiFullItemDialog gui, InfoStack info) {
				gui.controls.add(new GuiLabel("Nothing to select", 5, 30));
			}
			
			@Override
			public boolean canHandle(InfoStack info) {
				return info instanceof InfoFuel;
			}
		});
		
		registerGuiInfoHandler("Name", new GuiInfoHandler() {
			
			@Override
			public InfoStack parseInfo(SubGuiFullItemDialog gui, int stackSize) {
				return new InfoName(((GuiTextfield) gui.get("pattern")).text, stackSize);
			}
			
			@Override
			public void createControls(SubGuiFullItemDialog gui, InfoStack info) {
				gui.controls.add(new GuiTextfield("pattern", "", 0, 57, 144, 14));
			}
			
			@Override
			public boolean canHandle(InfoStack info) {
				return info instanceof InfoName;
			}
		});
	}
	
	private String name;
	
	public String getName() {
		return name;
	}
	
	public abstract boolean canHandle(InfoStack info);
	
	public abstract void createControls(SubGuiFullItemDialog gui, InfoStack info);
	
	public abstract InfoStack parseInfo(SubGuiFullItemDialog gui, int stackSize);
	
	public void onChanged(SubGuiFullItemDialog gui, GuiControlChangedEvent event) {}
	
}
