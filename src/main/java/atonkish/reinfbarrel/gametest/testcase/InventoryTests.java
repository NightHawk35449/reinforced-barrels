package atonkish.reinfbarrel.gametest.testcase;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;

import atonkish.reinfbarrel.ReinforcedBarrelsMod;
import atonkish.reinfbarrel.block.ModBlocks;
import atonkish.reinfbarrel.gametest.util.TestIdentifier;
import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.util.ReinforcingMaterials;

// Mostly CONFIRMED against decompiled reinforced-chests 26.1.2 InventoryTests.java: Text ->
// Component, BlockRotation -> Rotation, Identifier -> net.minecraft.resources.Identifier,
// BlockPos -> net.minecraft.core.BlockPos, size() -> getContainerSize() (confirmed via
// ReinforcedChestBlockEntity's own override), context.assertEquals -> context.assertValueEqual.
// NOT confirmed by chest (chest fetches its container via a different static-helper path,
// ChestBlock.getContainer(...), rather than pulling the block entity directly): context
// .setBlockState(pos, block) -> context.setBlock(pos, block), and getting the block entity
// itself. Real historical Mojang GameTestHelper only exposes a single-arg
// getBlockEntity(BlockPos), not a typed two-arg overload, so a cast is used instead of the old
// two-arg call. Confidence: Medium / Risk: Low (a compile error here is easy to diagnose and fix).
public class InventoryTests {
  private static final String TEST_ENVIRONMENT_DEFAULT =
      String.format("%s:inventory/default", ReinforcedBarrelsMod.MOD_ID);
  private static final String TEST_STRUCTURE_EMPTY = "fabric-gametest-api-v1:empty";

  public static final Collection<TestFunction> TEST_FUNCTIONS =
      new ArrayList<>() {
        {
          // Copper Barrel
          add(
              InventoryTests.createTest(
                  "Copper Barrel inventory size",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper")),
                  45));

          // Iron Barrel
          add(
              InventoryTests.createTest(
                  "Iron Barrel inventory size",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron")),
                  54));

          // Gold Barrel
          add(
              InventoryTests.createTest(
                  "Gold Barrel inventory size",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold")),
                  81));

          // Diamond Barrel
          add(
              InventoryTests.createTest(
                  "Diamond Barrel inventory size",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond")),
                  108));

          // Netherite Barrel
          add(
              InventoryTests.createTest(
                  "Netherite Barrel inventory size",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("netherite")),
                  108));
        }
      };

  private static TestFunction createTest(String name, Block barrelBlock, int size) {
    Identifier testIdentifier =
        TestIdentifier.of(ReinforcedBarrelsMod.MOD_ID, InventoryTests.class, name);

    return new TestFunction(
        testIdentifier,
        InventoryTests.TEST_ENVIRONMENT_DEFAULT,
        InventoryTests.TEST_STRUCTURE_EMPTY,
        20,
        0,
        true,
        Rotation.NONE,
        false,
        1,
        1,
        false,
        (context) -> {
          // Arrange
          BlockPos blockPos = BlockPos.ZERO;
          context.setBlock(blockPos, barrelBlock);

          // Act
          BarrelBlockEntity entity = context.getBlockEntity(blockPos, BarrelBlockEntity.class);

          // Assert
          try {
            context.assertValueEqual(
                entity.getContainerSize(),
                size,
                Component.literal(
                    String.format(
                        "%s inventory size",
                        Component.translatable(barrelBlock.getDescriptionId()).getString())));
          } catch (Exception e) {
            ReinforcedBarrelsMod.LOGGER.error("[{}] {}", testIdentifier, e.getMessage());
            throw e;
          }

          context.succeed();
        });
  }
}
