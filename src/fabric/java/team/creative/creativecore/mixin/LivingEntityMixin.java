package team.creative.creativecore.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import team.creative.creativecore.CreativeFabricLoader;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Definition(id = "useItem",
				field = "Lnet/minecraft/world/entity/LivingEntity;useItem:Lnet/minecraft/world/item/ItemStack;")
	@Definition(id = "finishUsingItem",
				method = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;")
	@Definition(id = "level",
				method = "Lnet/minecraft/world/entity/LivingEntity;level()Lnet/minecraft/world/level/Level;")
	@Expression("this.useItem.finishUsingItem(this.level(), this)")
	@WrapOperation(method = "completeUsingItem",
				   at = @At("MIXINEXTRAS:EXPRESSION"))
	public ItemStack hook(ItemStack instance, Level level, LivingEntity livingEntity, Operation<ItemStack> original) {
		var copy = instance.copy();
		original.call(instance, level, livingEntity);
		CreativeFabricLoader.FINISH_CONSUMING.invoker().accept(livingEntity, copy);
		return instance;
	}
}
