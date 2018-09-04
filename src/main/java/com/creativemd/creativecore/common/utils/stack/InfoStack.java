package com.creativemd.creativecore.common.utils.stack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

public abstract class InfoStack {
	
	public static HashMap<String, Class<? extends InfoStack>> types = new HashMap<>();
	
	public abstract static class InfoStackObjectParser {
		
		public abstract InfoStack parseObject(Object object); 
	}
	
	public static ArrayList<InfoStackObjectParser> objectParser = new ArrayList<>();
	
	public static void registerObjectParser(InfoStackObjectParser parser)
	{
		objectParser.add(parser);
	}
	
	public static void registerType(String id, Class<? extends InfoStack> classType)
	{
		if(types.containsKey(id))
			throw new IllegalArgumentException("Id '" + id + "' is already taken!");
		try {
			classType.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InvalidParameterException("The class does not contain an empty constructor!");
		}
		types.put(id, classType);
	}
	
	public static String getIDFromClass(Class<? extends InfoStack> classType)
	{
		for (Iterator<Entry<String, Class<? extends InfoStack>>> iterator = types.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Class<? extends InfoStack>> entry = iterator.next();
			if(entry.getValue() == classType)
				return entry.getKey();
			
		}
		System.out.println("Could not find type for class " + classType.getName());
		return "";
	}
	
	public static InfoStack parseObject(Object stack)
	{
		if(stack == null)
			return null;
		if(stack instanceof InfoStack)
			return (InfoStack) stack;
		
		for (int i = 0; i < objectParser.size(); i++) {
			InfoStack temp = null;
			try{
				temp = objectParser.get(i).parseObject(stack);
			}catch(Exception e){
				temp = null;
			}
			if(temp != null)
				return temp;
		}
		
		return null;
	}
	
	public static InfoStack parseNBT(NBTTagCompound nbt)
	{
		String id = nbt.getString("id");
		Class<? extends InfoStack> classType = types.get(id);
		if(classType == null)
			System.out.println("Missing class type=" + id);
		try {
			InfoStack info = classType.getConstructor().newInstance();
			info.loadFromNBT(nbt);
			return info;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static
	{
		//Load default types
		registerType("block", InfoBlock.class);
		registerObjectParser(new InfoStackObjectParser() {
			
			@Override
			public InfoStack parseObject(Object object) {
				Block block = null;
				if(object instanceof Block)
					block = (Block) object;
				if(object instanceof ItemBlock)
					block = Block.getBlockFromItem((Item) object);
				if(block != null && !(block instanceof BlockAir))
					return new InfoBlock(block);
				return null;
			}
		});
		
		registerType("item", InfoItem.class);
		registerObjectParser(new InfoStackObjectParser() {
			
			@Override
			public InfoStack parseObject(Object object) {
				if(object instanceof Item && !(object instanceof ItemBlock))
					return new InfoItem((Item) object);
				return null;
			}
		});
		
		registerType("itemstack", InfoItemStack.class);
		registerObjectParser(new InfoStackObjectParser() {
			
			@Override
			public InfoStack parseObject(Object object) {
				if(object instanceof ItemStack)
				{
					if(((ItemStack) object).getItemDamage() == OreDictionary.WILDCARD_VALUE)
					{
						if(((ItemStack) object).getItem() instanceof ItemBlock)
							return new InfoBlock(Block.getBlockFromItem(((ItemStack) object).getItem()));
						else
							return new InfoItem(((ItemStack) object).getItem());
					}
					return new InfoItemStack((ItemStack) object);
				}
				return null;
			}
		});
		
		registerType("material", InfoMaterial.class);
		registerObjectParser(new InfoStackObjectParser() {
			
			@Override
			public InfoStack parseObject(Object object) {
				if(object instanceof Material)
					return new InfoMaterial((Material) object);
				return null;
			}
		});
		
		registerType("ore", InfoOre.class);
		registerObjectParser(new InfoStackObjectParser() {
			
			@Override
			public InfoStack parseObject(Object object) {
				if(object instanceof String)
					return new InfoOre((String) object);
				if(object instanceof ArrayList)
				{
					ArrayList<Integer> oresIDs = new ArrayList<Integer>();
					ArrayList ores = (ArrayList) object;
					for (int i = 0; i < ores.size(); i++) {
						ArrayList<Integer> neworesIDs = new ArrayList<Integer>();
						int[] oreIDsofStack = OreDictionary.getOreIDs((ItemStack) ores.get(i));
						for (int j = 0; j < oreIDsofStack.length; j++)
							if(i == 0 || oresIDs.contains(oreIDsofStack[j]))
								neworesIDs.add(oreIDsofStack[j]);
						oresIDs = neworesIDs;
					}
					if(oresIDs.size() == 1)
						return new InfoOre(OreDictionary.getOreName(oresIDs.get(0)));
				}
				return null;
			}
		});
		
		registerType("name", InfoName.class);
		registerType("fuel", InfoFuel.class);
	}
	
	public String getID()
	{
		return getIDFromClass(getClass());
	}
	
	/**stacksize=0->stacksize ignored**/
	public int stackSize = 0;
	
	public InfoStack(int stackSize)
	{
		if(stackSize < 0)
			stackSize = 0;
		/*if(stackSize > 64)
			stackSize = 64;*/
		this.stackSize = stackSize;	
	}
	
	public InfoStack()
	{
		
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("id", getID());
		nbt.setInteger("count", stackSize);
		writeToNBTExtra(nbt);
		return nbt;
	}
	
	protected abstract void writeToNBTExtra(NBTTagCompound nbt);
	
	public void loadFromNBT(NBTTagCompound nbt)
	{
		stackSize = nbt.getInteger("count");
		loadFromNBTExtra(nbt);
	}
	
	protected abstract void loadFromNBTExtra(NBTTagCompound nbt);
	
	public int getAmount(ItemStack stack)
	{
		if(this.stackSize == 0)
			return Integer.MAX_VALUE;
		return stack.getCount()/stackSize;
	}
	
	public boolean isInstance(ItemStack stack)
	{
		if(isInstanceIgnoreSize(stack))
		{
			if(stackSize <= stack.getCount())
				return true;
		}
		return false;
	}
	
	public boolean isInstance(InfoStack info)
	{
		if(isInstanceIgnoreSize(info))
		{
			if(stackSize < info.stackSize)
				return false;
		}
		return false;
	}
	
	public abstract boolean isInstanceIgnoreSize(InfoStack info);
	
	public abstract InfoStack copy();
	
	public ItemStack getItemStack()
	{
		return getItemStack(stackSize);
	}
	
	/**
	 * Please don't use it often since some InfoStacks need to iterate through all blocks and items
	 * @return All possible ItemStacks, also using {@link #OreDictionary.WILDCARD_VALUE}
	 */
	public abstract ArrayList<ItemStack> getAllPossibleItemStacks();
	
	public abstract ItemStack getItemStack(int stacksize);
	
	protected abstract boolean isStackInstanceIgnoreSize(ItemStack stack);
	
	public boolean isInstanceIgnoreSize(ItemStack stack)
	{
		return isStackInstanceIgnoreSize(stack);
	}
	
	@Override
	public boolean equals(Object object)
	{
		return object instanceof InfoStack && ((InfoStack)object).stackSize == this.stackSize && equalsIgnoreSize(object);
	}
	
	public abstract boolean equalsIgnoreSize(Object object);
	
	protected static Field displayOnCreativeTab = ReflectionHelper.findField(Block.class, "displayOnCreativeTab", "field_149772_a");
	
	protected static List<ItemStack> getAllExistingItems()
	{
		NonNullList<ItemStack> stacks = NonNullList.create();
		Iterator iterator = Item.REGISTRY.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            item.getSubItems(item, item.getCreativeTab(), stacks);
        }
        
        iterator = Block.REGISTRY.iterator();

        while (iterator.hasNext())
        {
        	Block block = (Block)iterator.next();

            try {
				block.getSubBlocks(Item.getItemFromBlock(block), (CreativeTabs) displayOnCreativeTab.get(block), stacks);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
        }
        
        return stacks;
	}
	
}
