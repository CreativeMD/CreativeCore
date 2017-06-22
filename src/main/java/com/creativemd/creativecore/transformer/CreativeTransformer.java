package com.creativemd.creativecore.transformer;

import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

public abstract class CreativeTransformer implements IClassTransformer {
	
	public final String modid;
	
	public CreativeTransformer(String modid) {
		this.modid = modid;
		initTransformers();
	}
	
	private final ArrayList<Transformer> transformers = new ArrayList<>();
	
	protected abstract void initTransformers();
	
	protected void addTransformer(Transformer transformer)
	{
		transformers.add(transformer);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(name.contains("com.creativemd"))
			return basicClass;
		return transform(transformedName, basicClass);		
	}
	
	public byte[] transform(String name, byte[] basicClass)
	{
		int i = 0;
		while (i < transformers.size()) {
			if(transformers.get(i).is(name))
			{
				ClassNode classNode = new ClassNode();
				ClassReader classReader = new ClassReader(basicClass);
				classReader.accept(classNode, 0);
				
				transformers.get(i).transform(classNode);
				
				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
				classNode.accept(writer);
				basicClass = writer.toByteArray();
				
				System.out.println("[" + this.modid + "] Patched " + transformers.get(i).className + " ...");
				transformers.get(i).done();
				i++;
				//transformers.remove(i);
			}else
				i++;
		}
		return basicClass;
	}

}
