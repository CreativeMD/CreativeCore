function initializeCoreMod() {
	print("Init CreativeCore coremods ...")
    return {
        'clientnetwork': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.network.play.ClientPlayNetHandler',
				'methodName': 'func_195515_i',
				'methodDesc': '()Lcom/mojang/brigadier/CommandDispatcher;'
            },
            'transformer': function(method) {
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
				var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

				method.instructions.clear();
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/network/play/ClientPlayNetHandler", asmapi.mapField("field_195517_n"), "Lcom/mojang/brigadier/CommandDispatcher;"));
				method.instructions.add(asmapi.buildMethodCall("team/creative/creativecore/client/command/ClientCommandRegistry", "getDispatcher", "(Lcom/mojang/brigadier/CommandDispatcher;)Lcom/mojang/brigadier/CommandDispatcher;", asmapi.MethodType.STATIC));
				method.instructions.add(new InsnNode(Opcodes.ARETURN));

                return method;
            }
		},
		'tabInventory': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.gui.screen.Screen',
				'methodName': 'keyPressed',
				'methodDesc': '(III)Z'
            },
            'transformer': function(method) {
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var asmapi = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
				var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
				var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
				
				var node = asmapi.findFirstMethodCall(method, asmapi.MethodType.VIRTUAL, "net/minecraft/client/gui/screen/Screen", asmapi.mapMethod("changeFocus"), "(Z)Z");
				var label = node.getPrevious().getPrevious().getPrevious();
				if(label instanceof Java.type('org.objectweb.asm.tree.LineNumberNode'))
					label = label.getPrevious();
				
				method.instructions.insertBefore(label, asmapi.buildMethodCall("team/creative/creativecore/client/CreativeCoreClient", "keyTyped", "()Z", asmapi.MethodType.STATIC));
				method.instructions.insertBefore(label, new JumpInsnNode(Opcodes.IFEQ, label));
				method.instructions.insertBefore(label, new LabelNode());
				method.instructions.insertBefore(label, new InsnNode(Opcodes.ICONST_0));
				method.instructions.insertBefore(label, new InsnNode(Opcodes.IRETURN));

                return method;
            }
		}
    }
}
