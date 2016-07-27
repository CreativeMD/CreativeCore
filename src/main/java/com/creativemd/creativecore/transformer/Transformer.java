package com.creativemd.creativecore.transformer;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.world.biome.Biome;

public abstract class Transformer {
	
	public static ArrayList<Transformer> transformers = new ArrayList<>();
	
	public final String className;
	
	public Transformer(String className) {
		transformers.add(this);
		this.className = TransformerNames.patchClassName(className);
	}
	
	public boolean is(String className)
	{
		return className.equals(this.className);
	}
	
	public abstract void transform(ClassNode node);
	
	public MethodNode findMethod(ClassNode node, String name, String desc)
	{
		Iterator<MethodNode> methods = node.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if ((m.name.equals(name) && m.desc.equals(desc)))
				return m;
		}
		return null;
	}
	
	public FieldNode findField(ClassNode node, String name)
	{
		Iterator<FieldNode> fields = node.fields.iterator();
		while(fields.hasNext())
		{
			FieldNode f = fields.next();
			if ((f.name.equals(name)))
				return f;
		}
		return null;
	}
	
	public void done()
	{
		transformers.remove(this);
		if(transformers.size() == 0)
			TransformerNames.emptyLists();
	}
	
}
