function initializeCoreMod() {
    return {
        'clientnetwork': {
            'target': {
                'type': 'METHOD',
				'class': 'net.minecraft.client.multiplayer.ClientPacketListener',
				'methodName': 'm_105146_',
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
				method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/ClientPacketListener", asmapi.mapField("f_104899_"), "Lcom/mojang/brigadier/CommandDispatcher;"));
				method.instructions.add(asmapi.buildMethodCall("team/creative/creativecore/client/command/ClientCommandRegistry", "getDispatcher", "(Lcom/mojang/brigadier/CommandDispatcher;)Lcom/mojang/brigadier/CommandDispatcher;", asmapi.MethodType.STATIC));
				method.instructions.add(new InsnNode(Opcodes.ARETURN));
				
				asmapi.log("INFO", "Done");
				
                return method;
            }
		}
    }
}
