package com.creativemd.creativecore.common.utils.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.utils.stack.InfoBlock;
import com.creativemd.creativecore.common.utils.stack.InfoContainOre;
import com.creativemd.creativecore.common.utils.stack.InfoItem;
import com.creativemd.creativecore.common.utils.stack.InfoItemStack;
import com.creativemd.creativecore.common.utils.stack.InfoMaterial;
import com.creativemd.creativecore.common.utils.stack.InfoName;
import com.creativemd.creativecore.common.utils.stack.InfoOre;
import com.creativemd.creativecore.common.utils.stack.InfoStack;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@CreativeConfig
public class SortingList implements List<InfoStack> {
	
	@CreativeConfig
	public List<InfoStack> entries = new ArrayList<>();
	
	@CreativeConfig
	public boolean isWhitelist;
	
	public SortingList(SortingList list) {
		isWhitelist = list.isWhitelist;
		entries = new ArrayList<>(list.entries);
	}
	
	public SortingList() {
		this(true);
	}
	
	public SortingList(boolean isWhitelist) {
		this.isWhitelist = isWhitelist;
	}
	
	public boolean isBlacklist() {
		return !isWhitelist;
	}
	
	public boolean isWhitelist() {
		return isWhitelist;
	}
	
	public void setListType(boolean isWhitelist) {
		this.isWhitelist = isWhitelist;
	}
	
	public void setWhitelist() {
		isWhitelist = true;
	}
	
	public void setBlacklist() {
		isWhitelist = false;
	}
	
	/** The given array will be added to the list.
	 * 
	 * @param objects
	 *            the array can either contain a Material, a Block, an Item, an
	 *            ItemStack or a String for the OreDictionary. */
	public void addSortingObjects(Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			addSortingObject(objects[i]);
		}
	}
	
	/** The given parameter will be added to the list.
	 * 
	 * @param object
	 *            can either be a Material, a Block, an Item, an ItemStack or a
	 *            String for the OreDictionary. */
	public void addSortingObject(Object object) {
		if (object instanceof InfoStack) {
			add((InfoStack) object);
			return;
		}
		InfoStack info = InfoStack.parseObject(object);
		if (info != null)
			add(info);
	}
	
	/** The given parameter will be added to the list.
	 * 
	 * @param name
	 *            relates to blocks/items which contains this pattern in their name.
	 *            Example: "axe". */
	public void addSortingByNameContains(String name) {
		add(new InfoName(name));
	}
	
	/** The given parameter will be added to the list.
	 * 
	 * @param name
	 *            relates to ore which contains this pattern {@link #OreDictionary
	 *            OreDictionary}. Examples: "ingot" or "iron". */
	public void addSortingByOreContains(String name) {
		add(new InfoContainOre(name));
	}
	
	/** The given parameter will be added to the list.
	 * 
	 * @param name
	 *            relates to all objects which are connected to this ore inside the
	 *            {@link #OreDictionary OreDictionary}. Example: "ingotIron". */
	public void addSortingByOre(String ore) {
		add(new InfoOre(ore));
	}
	
	/** The given parameter will be added to the list.
	 * 
	 * @param block
	 *            relates to all equal Blocks. */
	public void addSortingByBlock(Block block) {
		add(new InfoBlock(block));
	}
	
	/** The given parameter will be added to the list.
	 * 
	 * @param item
	 *            relates to all equal Items. */
	public void addSortingByItem(Item item) {
		add(new InfoItem(item));
	}
	
	/** The given parameter will be added to the list. It is recommended to use
	 * {@link #addSortingObject addSortingObject} instead.
	 * 
	 * @param stack
	 *            relates to all equal ItemStacks. */
	public void addSortingByItemStack(ItemStack stack) {
		add(new InfoItemStack(stack));
	}
	
	/** The given parameter will be added to the list.
	 * 
	 * @param material
	 *            relates to all blocks which have the equal Material. */
	public void addSortingByMaterial(Material material) {
		add(new InfoMaterial(material));
	}
	
	protected boolean canBeFoundInList(Object object) {
		InfoStack info = InfoStack.parseObject(object);
		if (info == null)
			return false;
		for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
			InfoStack infoStack = (InfoStack) iterator.next();
			if (infoStack.isInstanceIgnoreSize(infoStack))
				return true;
		}
		return false;
	}
	
	protected boolean canBeFoundInList(ItemStack stack) {
		for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
			InfoStack infoStack = (InfoStack) iterator.next();
			if (infoStack.isInstanceIgnoreSize(stack))
				return true;
		}
		return false;
	}
	
	/** If the given object can pass the test. Whitelist: if it can be found.
	 * Blacklist: if it cannot be found.
	 * 
	 * @param object
	 */
	public boolean canPass(Object object) {
		return canBeFoundInList(object) == isWhitelist;
	}
	
	/** If the given itemstack can pass the test. Whitelist: if it can be found.
	 * Blacklist: if it cannot be found.
	 * 
	 * @param object
	 */
	public boolean canPass(ItemStack stack) {
		return canBeFoundInList(stack) == isWhitelist;
	}
	
	@Override
	public boolean add(InfoStack arg0) {
		return entries.add(arg0);
	}
	
	@Override
	public void add(int arg0, InfoStack arg1) {
		entries.add(arg0, arg1);
	}
	
	@Override
	public boolean addAll(Collection<? extends InfoStack> arg0) {
		return addAll(arg0);
	}
	
	@Override
	public boolean addAll(int arg0, Collection<? extends InfoStack> arg1) {
		return addAll(arg0, arg1);
	}
	
	@Override
	public void clear() {
		entries = new ArrayList<>();
	}
	
	@Override
	public boolean contains(Object arg0) {
		return entries.contains(arg0);
	}
	
	@Override
	public boolean containsAll(Collection<?> arg0) {
		return entries.containsAll(arg0);
	}
	
	@Override
	public InfoStack get(int arg0) {
		return entries.get(arg0);
	}
	
	@Override
	public int indexOf(Object arg0) {
		return entries.indexOf(arg0);
	}
	
	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}
	
	@Override
	public Iterator<InfoStack> iterator() {
		return entries.iterator();
	}
	
	@Override
	public int lastIndexOf(Object arg0) {
		return entries.lastIndexOf(arg0);
	}
	
	@Override
	public ListIterator<InfoStack> listIterator() {
		return entries.listIterator();
	}
	
	@Override
	public ListIterator<InfoStack> listIterator(int arg0) {
		return entries.listIterator(arg0);
	}
	
	@Override
	public boolean remove(Object arg0) {
		return entries.remove(arg0);
	}
	
	@Override
	public InfoStack remove(int arg0) {
		return entries.remove(arg0);
	}
	
	@Override
	public boolean removeAll(Collection<?> arg0) {
		return entries.removeAll(arg0);
	}
	
	@Override
	public boolean retainAll(Collection<?> arg0) {
		return entries.retainAll(arg0);
	}
	
	@Override
	public InfoStack set(int index, InfoStack element) {
		return entries.set(index, element);
	}
	
	@Override
	public int size() {
		return entries.size();
	}
	
	@Override
	public List<InfoStack> subList(int fromIndex, int toIndex) {
		return entries.subList(fromIndex, toIndex);
	}
	
	@Override
	public Object[] toArray() {
		return entries.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return entries.toArray(a);
	}
}
