package atonkish.reinfbarrel.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

import atonkish.reinfbarrel.block.entity.ReinforcedBarrelBlockEntity;
import atonkish.reinfbarrel.stat.ModStats;
import atonkish.reinfcore.util.ReinforcingMaterial;

// Package moves (net.minecraft.block -> net.minecraft.world.level.block, etc.),
// BlockState/World/PlayerEntity/BlockPos moves, and BlockHitResult moving to
// net.minecraft.world.phys are INFERRED from consistent, well-established Mojang mapping
// conventions and cross-checked against the packages actually used in the decompiled
// reinforced-chests jar (net.minecraft.core.BlockPos, net.minecraft.world.level.Level,
// net.minecraft.world.level.block.state.BlockState, net.minecraft.world.entity.player.Player
// are all CONFIRMED there). BlockHitResult's exact package and ActionResult -> InteractionResult
// are NOT decompiled evidence (chest's onUse-equivalent interaction is handled generically by
// vanilla ChestBlock/AbstractContainerMenu machinery, so it never appears in the chest source) -
// this is carried over from general knowledge of Mojang's real historical naming.
// player.openHandledScreen(...) -> player.openMenu(...) is likewise not decompiled evidence.
// Confidence: Medium / Risk: Medium on the un-decompiled parts (useWithoutItem name/signature,
// player.openMenu) - the real 26.1.2 compiler has since confirmed useWithoutItem compiles
// clean, and separately confirmed the real signature of angerNearbyPiglins as
// (ServerLevel, Player, boolean) via its own error message (from an earlier stale-duplicate-file
// mixup where an old 2-arg call surfaced this). The original 1.21 ReinforcedBarrelBlock.java
// genuinely calls PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true) itself - this
// is real original Barrel behavior to preserve, not something copied over from Chest (Chest's
// own source has no equivalent call, likely because its useWithoutItem-equivalent path differs
// from Barrel's, which explicitly overrides useWithoutItem here rather than relying on any
// default vanilla container-open flow).
public class ReinforcedBarrelBlock extends BarrelBlock {
  private final ReinforcingMaterial material;

  public ReinforcedBarrelBlock(ReinforcingMaterial material, BlockBehaviour.Properties settings) {
    super(settings);
    this.material = material;
  }

  @Override
  public InteractionResult useWithoutItem(
      BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
    if (world instanceof ServerLevel serverLevel
        && world.getBlockEntity(pos) instanceof BarrelBlockEntity barrelBlockEntity) {
      player.openMenu(barrelBlockEntity);
      player.awardStat(ModStats.OPEN_REINFORCED_BARREL_MAP.get(this.material));
      // RESTORED: real original 1.21 Barrel behavior (PiglinBrain.onGuardedBlockInteracted),
      // renamed to its confirmed 26.1.2 signature.
      PiglinAi.angerNearbyPiglins(serverLevel, player, true);
    }

    return InteractionResult.SUCCESS;
  }

  // CONFIRMED: chest's equivalent override is also named newBlockEntity (renamed from
  // createBlockEntity), same signature.
  @Override
  @Nullable public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ReinforcedBarrelBlockEntity(this.material, pos, state);
  }

  public ReinforcingMaterial getMaterial() {
    return this.material;
  }
}
