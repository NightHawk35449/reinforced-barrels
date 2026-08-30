package atonkish.reinfbarrel.mixin;

import java.util.Set;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// CONFIRMED: package move (net.minecraft.block.entity -> net.minecraft.world.level.block.entity)
// and accessor rename getBlocks() -> getValidBlocks() are both taken directly from the
// decompiled reinforced-chests 26.1.2 jar's BlockEntityTypeAccessor, which widens the same
// vanilla class (BlockEntityType) for the same purpose (registering a custom block into an
// existing vanilla BlockEntityType's valid-block set, i.e. BlockEntityType.BARREL here vs
// BlockEntityType.CHEST there). Evidence: High confidence / Risk: Low.
@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccessor {
  @Accessor
  Set<Block> getValidBlocks();
}
