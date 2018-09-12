package com.creativemd.creativecore.common.recipe;

public class CreativeShapelessRecipe {/*
                                       * extends CreativeRecipe{
                                       * 
                                       * public CreativeShapelessRecipe(ItemStack output, Object... input) {
                                       * super(output, 0, 0, input); }
                                       * 
                                       * public int getID(ItemStack stack) { for (int i = 0; i < input.length; i++) {
                                       * if(isStackValid(stack, input[i])) return i; } return -1; }
                                       * 
                                       * public int getIDList(ArrayList input, ItemStack stack) { for (int i = 0; i <
                                       * input.size(); i++) { if(isStackValid(stack, input.get(i))) return i; } return
                                       * -1; }
                                       * 
                                       * @Override protected ItemStack[] getObjectInValidOrder(IInventory inventory,
                                       * int InvWidth, int InvHeigt) { if(InvWidth * InvHeigt < this.input.length)
                                       * return null;
                                       * 
                                       * ItemStack[] inv = new ItemStack[this.input.length]; ArrayList tempInput = new
                                       * ArrayList(); for (int i = 0; i < this.input.length; i++) {
                                       * tempInput.add(this.input[i]); }
                                       * 
                                       * for (int i = 0; i < inventory.getSizeInventory(); i++) { ItemStack stack =
                                       * inventory.getStackInSlot(i); if(stack != null) { int id = getID(stack); int
                                       * listID = getIDList(tempInput, stack); if(id != -1 && listID != -1) { inv[id]
                                       * = stack; tempInput.remove(listID); } else return null; } }
                                       * if(tempInput.size() > 0) return null; return inv; }
                                       */
}
