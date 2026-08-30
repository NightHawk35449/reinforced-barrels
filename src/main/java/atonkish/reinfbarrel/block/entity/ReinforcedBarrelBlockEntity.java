package atonkish.reinfbarrel.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import atonkish.reinfcore.util.ReinforcingMaterial;

// Mostly CONFIRMED against decompiled reinforced-chests 26.1.2 ReinforcedChestBlockEntity.java,
// which is structurally the closest sibling to this class:
//  - Constructor: CONFIRMED BY THE REAL 26.1.2 COMPILER that BarrelBlockEntity, unlike
//    ChestBlockEntity, does NOT expose a 3-arg (BlockEntityType<?>, BlockPos, BlockState)
//    constructor - only the vanilla 2-arg (BlockPos, BlockState) one. This is a genuine
//    difference between Barrel and Chest at the real vanilla API level, not a guess either
//    way. The type is set afterward via the restored BlockEntityAccessor#setType mixin,
//    matching the original 1.21 barrel mod's own workaround for the same constraint.
//  - ViewerCountManager -> ContainerOpenersCounter, with onContainerOpen/onContainerClose/
//    onViewerCountUpdate/isPlayerViewing -> onOpen/onClose/openerCountChanged/isOwnContainer,
//    and openContainer/closeContainer/updateViewerCount -> incrementOpeners/decrementOpeners/
//    recheckOpeners - CONFIRMED, chest uses the exact same renamed API.
//  - asLivingEntity() -> getLivingEntity() on ContainerUser - CONFIRMED via chest.
//  - this.removed -> this.remove, getWorld()/getPos()/getCachedState() -> getLevel()/
//    getBlockPos()/getBlockState(), createScreenHandler(...) -> createMenu(...),
//    getContainerName() -> getDefaultName(), size() -> getContainerSize(),
//    setHeldStacks(DefaultedList...) -> setItems(NonNullList...),
//    player.currentScreenHandler -> player.containerMenu - all CONFIRMED via chest.
// NOT confirmed by either decompiled jar (barrel-specific, no chest equivalent):
//  - state.with(...) -> state.setValue(...): high-confidence inferred from chest's
//    state.getValue(...) usage and standard Mojang BlockState naming.
//  - world.setBlockState(pos, state, flags) -> level.setBlock(pos, state, flags), and
//    Block.NOTIFY_ALL -> Block.UPDATE_ALL: inferred from general Mojang-mapping conventions,
//    NOT decompiled evidence. Verify against the real jar/genSources.
//  - Direction.getVector() -> Direction.getNormal(): inferred, not decompiled evidence.
// Confidence on the "NOT confirmed" block: Medium / Risk: Medium - please verify these four
// against genSources before treating them as final.
public class ReinforcedBarrelBlockEntity extends BarrelBlockEntity {
  private final ContainerOpenersCounter stateManager;
  private final ReinforcingMaterial cachedMaterial;

  public ReinforcedBarrelBlockEntity(ReinforcingMaterial material, BlockPos pos, BlockState state) {
    // CONFIRMED by the 26.1.2 compiler: BarrelBlockEntity only has the vanilla 2-arg
    // (BlockPos, BlockState) constructor, unlike ChestBlockEntity. The correct per-material
    // type is applied afterward via the restored BlockEntityAccessor#setType mixin, matching
    // the original 1.21 barrel mod's approach (which needed the same workaround for the same
    // reason - Chest's newer 3-arg constructor was a Chest-specific difference, not a general
    // 26.1.2 pattern).
    super(pos, state);
    ((atonkish.reinfbarrel.mixin.BlockEntityAccessor) this)
        .setType(ModBlockEntityType.REINFORCED_BARREL_MAP.get(material));
    this.setItems(NonNullList.withSize(material.getSize(), ItemStack.EMPTY));
    this.stateManager =
        new ContainerOpenersCounter() {
          @Override
          protected void onOpen(Level level, BlockPos pos, BlockState state) {
            ReinforcedBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
            ReinforcedBarrelBlockEntity.this.setOpen(state, true);
          }

          @Override
          protected void onClose(Level level, BlockPos pos, BlockState state) {
            ReinforcedBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
            ReinforcedBarrelBlockEntity.this.setOpen(state, false);
          }

          @Override
          protected void openerCountChanged(
              Level level,
              BlockPos pos,
              BlockState state,
              int oldViewerCount,
              int newViewerCount) {}

          @Override
          public boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ReinforcedStorageScreenHandler) {
              Container inventory =
                  ((ReinforcedStorageScreenHandler) player.containerMenu).getInventory();
              return inventory == ReinforcedBarrelBlockEntity.this;
            } else {
              return false;
            }
          }
        };
    this.cachedMaterial = material;
  }

  @Override
  public int getContainerSize() {
    return this.cachedMaterial.getSize();
  }

  @Override
  protected Component getDefaultName() {
    // CONFIRMED: chest's getDefaultName() uses BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(...),
    // not a static BlockEntityType.getKey/getId helper.
    String namespace = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType()).getNamespace();
    return Component.translatable(
        "container." + namespace + "." + this.cachedMaterial.getName() + "Barrel");
  }

  @Override
  protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
    return ReinforcedStorageScreenHandler.createSingleBlockScreen(
        this.cachedMaterial, containerId, inventory, this);
  }

  @Override
  public void startOpen(ContainerUser user) {
    if (!this.remove && !user.getLivingEntity().isSpectator()) {
      this.stateManager.incrementOpeners(
          user.getLivingEntity(),
          this.getLevel(),
          this.getBlockPos(),
          this.getBlockState(),
          user.getContainerInteractionRange());
    }
  }

  @Override
  public void stopOpen(ContainerUser user) {
    if (!this.remove && !user.getLivingEntity().isSpectator()) {
      this.stateManager.decrementOpeners(
          user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
    }
  }

  public void recheckOpen() {
    if (!this.remove) {
      this.stateManager.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
    }
  }

  private void setOpen(BlockState state, boolean open) {
    this.level.setBlock(
        this.getBlockPos(), state.setValue(BarrelBlock.OPEN, open), Block.UPDATE_ALL);
  }

  private void playSound(BlockState state, SoundEvent soundEvent) {
    Direction direction = state.getValue(BarrelBlock.FACING);
    double d = (double) this.getBlockPos().getX() + 0.5D + (double) direction.getStepX() / 2.0D;
    double e = (double) this.getBlockPos().getY() + 0.5D + (double) direction.getStepY() / 2.0D;
    double f = (double) this.getBlockPos().getZ() + 0.5D + (double) direction.getStepZ() / 2.0D;
    this.level.playSound(
        null,
        d,
        e,
        f,
        soundEvent,
        SoundSource.BLOCKS,
        0.5F,
        this.level.getRandom().nextFloat() * 0.1F + 0.9F);
  }

  public ReinforcingMaterial getMaterial() {
    return this.cachedMaterial;
  }
}
