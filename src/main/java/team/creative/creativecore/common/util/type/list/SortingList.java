package team.creative.creativecore.common.util.type.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientBlock;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientBlockTag;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientItem;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientItemStack;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientItemTag;

@CreativeConfig
public class SortingList implements List<CreativeIngredient> {
    
    @CreativeConfig
    public List<CreativeIngredient> entries = new ArrayList<>();
    
    @CreativeConfig
    public boolean isWhitelist;
    
    public SortingList(SortingList list) {
        this.isWhitelist = list.isWhitelist;
        this.entries = new ArrayList<>(list.entries);
    }
    
    public SortingList() {
        this(true);
    }
    
    public SortingList(boolean isWhitelist) {
        this.isWhitelist = isWhitelist;
    }
    
    public boolean isBlacklist() {
        return !isWhitelist();
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
        for (int i = 0; i < objects.length; i++)
            addSortingObject(objects[i]);
    }
    
    /** The given parameter will be added to the list.
     * 
     * @param object
     *            can either be a Material, a Block, an Item, an ItemStack or a
     *            String for the OreDictionary. */
    public void addSortingObject(Object object) {
        if (object instanceof CreativeIngredient ingredient) {
            add(ingredient);
            return;
        }
        CreativeIngredient info = CreativeIngredient.parse(object);
        if (info != null)
            add(info);
    }
    
    /** The given parameter will be added to the list.
     * 
     * @param block
     *            relates to all equal Blocks. */
    public void addSortingByBlock(Block block) {
        add(new CreativeIngredientBlock(block));
    }
    
    /** The given parameter will be added to the list.
     * 
     * @param item
     *            relates to all equal Items. */
    public void addSortingByItem(Item item) {
        add(new CreativeIngredientItem(item));
    }
    
    /** The given parameter will be added to the list. It is recommended to use
     * {@link #addSortingObject addSortingObject} instead.
     * 
     * @param stack
     *            relates to all equal ItemStacks. */
    public void addSortingByItemStack(ItemStack stack) {
        add(new CreativeIngredientItemStack(stack, false));
    }
    
    /** The given parameter will be added to the list.
     * 
     * @param tag
     *            relates to all blocks which have the tag. */
    public void addSortingByBlockTag(TagKey<Block> tag) {
        add(new CreativeIngredientBlockTag(tag));
    }
    
    /** The given parameter will be added to the list.
     * 
     * @param tag
     *            relates to all items which have the tag. */
    public void addSortingByItemTag(TagKey<Item> tag) {
        add(new CreativeIngredientItemTag(tag));
    }
    
    protected boolean canBeFoundInList(Object object) {
        CreativeIngredient info = CreativeIngredient.parse(object);
        if (info == null)
            return false;
        for (CreativeIngredient ingredient : entries)
            if (ingredient.is(info))
                return true;
        return false;
    }
    
    protected boolean canBeFoundInList(ItemStack stack) {
        for (CreativeIngredient ingredient : entries)
            if (ingredient.is(stack))
                return true;
        return false;
    }
    
    /** If the given object can pass the test. Whitelist: if it can be found.
     * Blacklist: if it cannot be found.
     * 
     * @param object
     *            object to check */
    public boolean canPass(Object object) {
        return canBeFoundInList(object) == isWhitelist;
    }
    
    /** If the given itemstack can pass the test. Whitelist: if it can be found.
     * Blacklist: if it cannot be found.
     *
     * @param stack
     *            item stack */
    public boolean canPass(ItemStack stack) {
        return canBeFoundInList(stack) == isWhitelist;
    }
    
    @Override
    public boolean add(CreativeIngredient arg0) {
        return entries.add(arg0);
    }
    
    @Override
    public void add(int arg0, CreativeIngredient arg1) {
        entries.add(arg0, arg1);
    }
    
    @Override
    public boolean addAll(Collection<? extends CreativeIngredient> arg0) {
        return entries.addAll(arg0);
    }
    
    @Override
    public boolean addAll(int arg0, Collection<? extends CreativeIngredient> arg1) {
        return entries.addAll(arg0, arg1);
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
    public CreativeIngredient get(int arg0) {
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
    public Iterator<CreativeIngredient> iterator() {
        return entries.iterator();
    }
    
    @Override
    public int lastIndexOf(Object arg0) {
        return entries.lastIndexOf(arg0);
    }
    
    @Override
    public ListIterator<CreativeIngredient> listIterator() {
        return entries.listIterator();
    }
    
    @Override
    public ListIterator<CreativeIngredient> listIterator(int arg0) {
        return entries.listIterator(arg0);
    }
    
    @Override
    public boolean remove(Object arg0) {
        return entries.remove(arg0);
    }
    
    @Override
    public CreativeIngredient remove(int arg0) {
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
    public CreativeIngredient set(int index, CreativeIngredient element) {
        return entries.set(index, element);
    }
    
    @Override
    public int size() {
        return entries.size();
    }
    
    @Override
    public List<CreativeIngredient> subList(int fromIndex, int toIndex) {
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
