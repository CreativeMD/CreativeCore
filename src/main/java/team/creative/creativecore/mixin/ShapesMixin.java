package team.creative.creativecore.mixin;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.creative.creativecore.CreativeCore;
import team.creative.creativecore.common.util.math.box.ABB;
import team.creative.creativecore.common.util.math.box.ABBs;
import team.creative.creativecore.common.util.math.box.BoxesVoxelShape;
import team.creative.creativecore.common.util.type.list.MarkIterator;
import team.creative.creativecore.common.util.type.list.MarkList;

import java.util.List;

@Mixin(Shapes.class)
public class ShapesMixin {
    
    private static final String createIndexMerger = "Lnet/minecraft/world/phys/shapes/Shapes;createIndexMerger(ILit/unimi/dsi/fastutil/doubles/DoubleList;Lit/unimi/dsi/fastutil/doubles/DoubleList;ZZ)Lnet/minecraft/world/phys/shapes/IndexMerger;";
    
    @Inject(method = "joinUnoptimized(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At(value = "INVOKE", target = createIndexMerger), cancellable = true, require = 1)
    private static void joinUnoptimized(VoxelShape shape1, VoxelShape shape2, BooleanOp operation, CallbackInfoReturnable<VoxelShape> info) {
        
        if (!(shape1 instanceof BoxesVoxelShape) && !(shape2 instanceof BoxesVoxelShape))
            return;
        
        if (operation == BooleanOp.AND) {
            ABBs bbs = new ABBs(shape1);
            if (shape2 instanceof BoxesVoxelShape bb2)
                bbs.intersection(bb2.boxes);
            else
                bbs.intersectionVanilla(shape2.toAabbs());
            info.setReturnValue(BoxesVoxelShape.create(bbs.getBoxes()));
            return;
        } else if (operation == BooleanOp.NOT_AND) {
            ABBs intersection = new ABBs(shape1);
            if (shape2 instanceof BoxesVoxelShape bb2)
                intersection.intersection(bb2.boxes);
            else
                intersection.intersectionVanilla(shape2.toAabbs());
            ABBs bbs = new ABBs(shape1);
            bbs.addShape(shape2);
            bbs.cutOut(intersection);
            info.setReturnValue(BoxesVoxelShape.create(bbs.getBoxes()));
            return;
        } else if (operation == BooleanOp.OR) {
            ABBs bbs = new ABBs(shape1);
            if (shape2 instanceof BoxesVoxelShape bb2)
                bbs.addNonOverlapping(bb2.boxes);
            else
                bbs.addNonOverlappingVanilla(shape2.toAabbs());
            info.setReturnValue(BoxesVoxelShape.create(bbs.getBoxes()));
            return;
        } else if (operation == BooleanOp.FIRST) {
            if (shape1 instanceof BoxesVoxelShape bb1)
                info.setReturnValue(BoxesVoxelShape.create(bb1.boxes));
            else
                info.setReturnValue(BoxesVoxelShape.createVanilla(shape1.toAabbs()));
            return;
        } else if (operation == BooleanOp.SECOND) {
            if (shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(BoxesVoxelShape.create(bb2.boxes));
            else
                info.setReturnValue(BoxesVoxelShape.createVanilla(shape2.toAabbs()));
            return;
        } else if (operation == BooleanOp.ONLY_FIRST) {
            ABBs bbs = new ABBs(shape1);
            if (shape2 instanceof BoxesVoxelShape bb2)
                bbs.cutOut(bb2.boxes);
            else
                bbs.cutOutVanilla(shape2.toAabbs());
            info.setReturnValue(BoxesVoxelShape.create(bbs.getBoxes()));
            return;
        } else if (operation == BooleanOp.ONLY_SECOND) {
            ABBs bbs = new ABBs(shape2);
            if (shape1 instanceof BoxesVoxelShape bb1)
                bbs.cutOut(bb1.boxes);
            else
                bbs.cutOutVanilla(shape1.toAabbs());
            info.setReturnValue(BoxesVoxelShape.create(bbs.getBoxes()));
            return;
        } else
            CreativeCore.LOGGER.warn("Apparentely there are more BooleanOp " + operation + "," + operation.getClass().getName() + ", which might result in horrible performance.");
        
    }
    
    private static boolean same(List<ABB> boxes1, List<ABB> boxes2) {
        MarkList<ABB> marked = new MarkList<>(boxes1);
        outer: for (ABB second : boxes2) {
            for (MarkIterator<ABB> iterator = marked.iterator(); iterator.hasNext();) {
                ABB abb = iterator.next();
                if (second.equals(abb)) {
                    iterator.mark();
                    continue outer;
                }
                
            }
            return false;
        }
        return true;
    }
    
    private static boolean sameVanilla(List<ABB> boxes1, List<AABB> boxes2) {
        MarkList<ABB> marked = new MarkList<>(boxes1);
        outer: for (AABB second : boxes2) {
            for (MarkIterator<ABB> iterator = marked.iterator(); iterator.hasNext();) {
                ABB abb = iterator.next();
                if (abb.equals(second)) {
                    iterator.mark();
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }
    
    private static boolean intersects(Iterable<ABB> boxes1, Iterable<ABB> boxes2) {
        for (ABB first : boxes1)
            for (ABB second : boxes2)
                if (first.intersects(second))
                    return true;
        return false;
    }
    
    private static boolean intersectsVanilla(Iterable<ABB> boxes1, Iterable<AABB> boxes2) {
        for (ABB first : boxes1)
            for (AABB second : boxes2)
                if (first.intersects(second))
                    return true;
        return false;
    }
    
    @Inject(method = "joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z",
            at = @At(value = "INVOKE", target = createIndexMerger), cancellable = true, require = 1)
    private static void joinIsNotEmpty(VoxelShape shape1, VoxelShape shape2, BooleanOp operation, CallbackInfoReturnable<Boolean> info) {
        
        if (!(shape1 instanceof BoxesVoxelShape) && !(shape2 instanceof BoxesVoxelShape))
            return;
        
        if (operation == BooleanOp.AND) {
            if (shape1 instanceof BoxesVoxelShape bb1 && shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(intersects(bb1.boxes, bb2.boxes));
            else if (shape1 instanceof BoxesVoxelShape bb1)
                info.setReturnValue(intersectsVanilla(bb1.boxes, shape2.toAabbs()));
            else if (shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(intersectsVanilla(bb2.boxes, shape1.toAabbs()));
            return;
        } else if (operation == BooleanOp.NOT_AND) {
            if (shape1 instanceof BoxesVoxelShape bb1 && shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(!intersects(bb1.boxes, bb2.boxes));
            else if (shape1 instanceof BoxesVoxelShape bb1)
                info.setReturnValue(!intersectsVanilla(bb1.boxes, shape2.toAabbs()));
            else if (shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(!intersectsVanilla(bb2.boxes, shape1.toAabbs()));
            return;
        } else if (operation == BooleanOp.OR) {
            info.setReturnValue(!shape1.isEmpty() || !shape2.isEmpty());
            return;
        } else if (operation == BooleanOp.FIRST) {
            info.setReturnValue(!shape1.isEmpty());
            return;
        } else if (operation == BooleanOp.SECOND) {
            info.setReturnValue(!shape2.isEmpty());
            return;
        } else if (operation == BooleanOp.SAME) {
            if (shape1 instanceof BoxesVoxelShape bb1 && shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(same(bb1.boxes, bb2.boxes));
            else if (shape1 instanceof BoxesVoxelShape bb1)
                info.setReturnValue(sameVanilla(bb1.boxes, shape2.toAabbs()));
            else if (shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(sameVanilla(bb2.boxes, shape1.toAabbs()));
            return;
        } else if (operation == BooleanOp.NOT_SAME) {
            if (shape1 instanceof BoxesVoxelShape bb1 && shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(!same(bb1.boxes, bb2.boxes));
            else if (shape1 instanceof BoxesVoxelShape bb1)
                info.setReturnValue(!sameVanilla(bb1.boxes, shape2.toAabbs()));
            else if (shape2 instanceof BoxesVoxelShape bb2)
                info.setReturnValue(!sameVanilla(bb2.boxes, shape1.toAabbs()));
            return;
        } else if (operation == BooleanOp.ONLY_FIRST) {
            ABBs bbs = new ABBs(shape1);
            if (shape2 instanceof BoxesVoxelShape bb2)
                bbs.cutOut(bb2.boxes);
            else
                bbs.cutOutVanilla(shape2.toAabbs());
            info.setReturnValue(bbs.isEmpty());
            return;
        } else if (operation == BooleanOp.ONLY_SECOND) {
            ABBs bbs = new ABBs(shape2);
            if (shape1 instanceof BoxesVoxelShape bb1)
                bbs.cutOut(bb1.boxes);
            else
                bbs.cutOutVanilla(shape1.toAabbs());
            info.setReturnValue(bbs.isEmpty());
            return;
        } else
            CreativeCore.LOGGER.warn("Apparentely there are more BooleanOp " + operation + "," + operation.getClass().getName() + ", which might result in horrible performance.");
    }
    
}
