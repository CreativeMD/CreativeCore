package com.creativemd.creativecore.core;

import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.creativemd.creativecore.transformer.CreativeTransformer;
import com.creativemd.creativecore.transformer.Transformer;
import com.creativemd.creativecore.transformer.TransformerNames;

public class CreativeCoreTransformer extends CreativeTransformer {

	public CreativeCoreTransformer() {
		super("creativecore");
	}

	@Override
	protected void initTransformers() {
			
			addTransformer(new Transformer("net.minecraft.world.chunk.Chunk") {
			
			@Override
			public void transform(ClassNode node) {
				MethodNode m = findMethod(node, "onChunkUnload", "()V");
				
				String fieldOwner = patchClassName("net/minecraft/world/chunk/Chunk");
				String fieldName = patchFieldName("chunkTileEntityMap");
				
				boolean found = false;
				
				Iterator<AbstractInsnNode> iterator = m.instructions.iterator();
				while(iterator.hasNext())
				{
					AbstractInsnNode insn = iterator.next();
					if(insn instanceof FieldInsnNode && insn.getOpcode() == Opcodes.GETFIELD && ((FieldInsnNode) insn).owner.equals(fieldOwner) && ((FieldInsnNode) insn).name.equals(fieldName))
						found = true;
					if(found)
					{
						iterator.remove();
						if(insn instanceof JumpInsnNode && insn.getOpcode() == Opcodes.GOTO)
							break;
					}
				}
			}
		});
		
		addTransformer(new Transformer("net.minecraft.world.World") {
			
			@Override
			public void transform(ClassNode node) {
				
				String worldClassName = patchClassName("net/minecraft/world/World");
				String fieldDESC = patchDESC("Ljava/util/List;");
				String fieldNameTicking = patchFieldName("tickableTileEntities");
				String fieldNameLoaded = patchFieldName("loadedTileEntityList");
				
				node.fields.add(new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL, "tileEntitiesFromChunkToBeRemoved",  patchDESC("Ljava/util/List;"), patchDESC("Ljava/util/List<Lnet/minecraft/tileentity/TileEntity;>;"), null));
				
				Iterator<MethodNode> iterator = node.methods.iterator();
				while(iterator.hasNext())
				{
					AbstractInsnNode toInsert = null;
					MethodNode m = iterator.next();
					if(m.name.equals("<init>"))
					{
						Iterator<AbstractInsnNode> iterator2 = m.instructions.iterator();
						while(iterator2.hasNext())
						{
							AbstractInsnNode insn = iterator2.next();
							if(insn instanceof FieldInsnNode && insn.getOpcode() == Opcodes.PUTFIELD && ((FieldInsnNode) insn).owner.equals(worldClassName) && (((FieldInsnNode) insn).name.equals(fieldNameLoaded) || ((FieldInsnNode) insn).name.equals(fieldNameTicking)))
							{
								MethodInsnNode m2 = (MethodInsnNode) insn.getPrevious();
								m2.name = "createTileEntity";
								m2.owner = "com/creativemd/creativecore/common/world/WorldInteractor";
								m2.desc = patchDESC("()Ljava/util/List;");
								m2.setOpcode(Opcodes.INVOKESTATIC);
								if(toInsert == null)
									toInsert = insn;
							}
						}
						if(toInsert != null)
						{
							m.instructions.insert(toInsert, new FieldInsnNode(Opcodes.PUTFIELD, worldClassName, "tileEntitiesFromChunkToBeRemoved", fieldDESC));
							m.instructions.insert(toInsert, new MethodInsnNode(Opcodes.INVOKESPECIAL, patchDESC("java/util/ArrayList"), "<init>", "()V", false));
							m.instructions.insert(toInsert, new InsnNode(Opcodes.DUP));
							m.instructions.insert(toInsert, new TypeInsnNode(Opcodes.NEW, patchDESC("java/util/ArrayList")));
							m.instructions.insert(toInsert, new VarInsnNode(Opcodes.ALOAD, 0));
							m.instructions.insert(new LabelNode());
							toInsert = null;
						}
					}
					
				}
				
				String fieldName = patchFieldName("tileEntitiesToBeRemoved");
				String methodOwner = patchClassName("java/util/List");
				
				String tickMethodOwner = patchClassName("net/minecraft/util/ITickable");
				String tickMethodName = TransformerNames.patchMethodName("update", "()V", tickMethodOwner);
				
				MethodNode m = findMethod(node, "updateEntities", "()V");
				Iterator<AbstractInsnNode> iterator2 = m.instructions.iterator();
				AbstractInsnNode toInsert = null;
				AbstractInsnNode invokeUpdate = null;
				
				while(iterator2.hasNext())
				{
					AbstractInsnNode insn = iterator2.next();
					
					if(insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals(tickMethodOwner) && ((MethodInsnNode) insn).name.equals(tickMethodName))
						invokeUpdate = insn;
					
					if(insn instanceof FieldInsnNode && insn.getOpcode() == Opcodes.GETFIELD && ((FieldInsnNode) insn).owner.equals(worldClassName) && ((FieldInsnNode) insn).name.equals(fieldName))
					{
						AbstractInsnNode next = insn.getNext();
						if(next instanceof MethodInsnNode && ((MethodInsnNode) next).owner.equals(methodOwner) && ((MethodInsnNode) next).name.equals("isEmpty"))
						{
							toInsert = insn.getPrevious();
						}
					}
				}
				
				m.instructions.insertBefore(toInsert, new VarInsnNode(Opcodes.ALOAD, 0));
				m.instructions.insertBefore(toInsert, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/creativecore/common/world/WorldInteractor", "removeTileEntities", patchDESC("(Lnet/minecraft/world/World;)V"), false));
				
				//Surround update method
				LabelNode addBefore = findPreviousLabel(invokeUpdate);
				LabelNode after = findNextLabel(invokeUpdate);
				
				int indexOfTileEntityVar = getIndexOfVar(m, "Lnet/minecraft/tileentity/TileEntity;");
				int indexOfIteratorVar = getIndexOfVar(m, "Ljava/util/Iterator;", "Ljava/util/Iterator<Lnet/minecraft/tileentity/TileEntity;>;");
				
				m.instructions.insertBefore(addBefore, new VarInsnNode(Opcodes.ALOAD, indexOfTileEntityVar));
				m.instructions.insertBefore(addBefore, new TypeInsnNode(Opcodes.INSTANCEOF, "com/creativemd/creativecore/common/tileentity/ICustomTickable"));
				m.instructions.insertBefore(addBefore, new JumpInsnNode(Opcodes.IFEQ, addBefore));
				m.instructions.insertBefore(addBefore, new VarInsnNode(Opcodes.ALOAD, indexOfTileEntityVar));
				m.instructions.insertBefore(addBefore, new MethodInsnNode(Opcodes.INVOKEINTERFACE, "com/creativemd/creativecore/common/tileentity/ICustomTickable", "shouldTick", "()Z", true));
				m.instructions.insertBefore(addBefore, new JumpInsnNode(Opcodes.IFNE, addBefore));
				LabelNode conditionRemove = new LabelNode();
				m.instructions.insertBefore(addBefore, conditionRemove);
				m.instructions.insertBefore(addBefore, new VarInsnNode(Opcodes.ALOAD, indexOfIteratorVar));
				m.instructions.insertBefore(addBefore, new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "remove", "()V", true));
				m.instructions.insertBefore(addBefore, new JumpInsnNode(Opcodes.GOTO, after));
			}
		});
	}

}
