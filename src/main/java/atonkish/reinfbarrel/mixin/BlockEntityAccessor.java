package atonkish.reinfbarrel.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

// RESTORED (with updated package only) from the original 1.21 barrel mixin, which targeted
// this exact class/field ("type" on BlockEntity) for the exact same reason. The 26.1.2
// compiler CONFIRMED this is needed: BarrelBlockEntity's constructor only accepts
// (BlockPos, BlockState) - unlike ChestBlockEntity, which reinforced-chests' decompiled
// 26.1.2 source shows DOES expose a (BlockEntityType<?>, BlockPos, BlockState) constructor.
// Barrel and Chest diverge here at the real vanilla API level, confirmed directly by the
// compiler's "constructor BarrelBlockEntity in class BarrelBlockEntity cannot be applied to
// given types; required: BlockPos,BlockState" error - not a guess.
// Field name "type" is INFERRED (carried over unchanged from the 1.21 mixin, which worked
// against the exact same base BlockEntity class this targets) - standard, stable Mojang
// field naming for BlockEntity's own type field, not expected to have changed, but only
// verifiable for certain via a successful Mixin apply at runtime (Mixin accessors are not
// checked by javac).
@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {
  @Mutable
  @Accessor("type")
  public void setType(BlockEntityType<?> type);
}
