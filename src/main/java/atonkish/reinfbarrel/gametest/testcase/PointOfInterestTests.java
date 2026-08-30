package atonkish.reinfbarrel.gametest.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import atonkish.reinfbarrel.ReinforcedBarrelsMod;
import atonkish.reinfbarrel.block.ModBlocks;
import atonkish.reinfbarrel.gametest.util.TestIdentifier;
import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.util.ReinforcingMaterials;

// CONFIRMED: Villager and VillagerProfession moved to net.minecraft.world.entity.npc.villager
// as of the 1.21.11 vanilla update (part of a broad net.minecraft.world.entity subpackage
// reorganization), per the official NeoForged Mojang-mappings migration primer
// (docs.neoforged.net/primer/docs/1.21.11/). Verified this doesn't affect any other class used
// elsewhere in this project - cross-checked every net.minecraft.world.entity.* import in the
// codebase against that primer's full rename list; nothing else was in scope (Player/Inventory
// stay in .player, EntityType/EquipmentSlot/ContainerUser are untouched top-level classes,
// MemoryModuleType and PoiType/PoiTypes were not part of this particular reorg, and Piglin
// already lived in its own subpackage beforehand).
// Everything else in this file (BlockRotation -> Rotation, context.setBlockState ->
// context.setBlock, context.spawnEntity -> context.spawn, context.runAtTick ->
// context.runAtTickTime, context.assertEquals -> context.assertValueEqual, context.complete()
// -> context.succeed(), Text -> Component) IS confirmed via the other gametest files
// cross-checked against reinforced-chests.
public class PointOfInterestTests {
  private static final String TEST_ENVIRONMENT_DEFAULT =
      String.format("%s:point_of_interest/default", ReinforcedBarrelsMod.MOD_ID);
  private static final String TEST_STRUCTURE_EMPTY = "fabric-gametest-api-v1:empty";

  public static final Collection<TestFunction> TEST_FUNCTIONS =
      new ArrayList<>() {
        {
          // Copper Barrel
          add(
              PointOfInterestTests.createTest(
                  "Villager have a fisherman profession at Copper Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper"))));

          // Iron Barrel
          add(
              PointOfInterestTests.createTest(
                  "Villager have a fisherman profession at Iron Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron"))));

          // Gold Barrel
          add(
              PointOfInterestTests.createTest(
                  "Villager have a fisherman profession at Gold Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold"))));

          // Diamond Barrel
          add(
              PointOfInterestTests.createTest(
                  "Villager have a fisherman profession at Diamond Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond"))));

          // Netherite Barrel
          add(
              PointOfInterestTests.createTest(
                  "Villager have a fisherman profession at Netherite Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("netherite"))));
        }
      };

  private static TestFunction createTest(String name, Block barrelBlock) {
    Identifier testIdentifier =
        TestIdentifier.of(ReinforcedBarrelsMod.MOD_ID, PointOfInterestTests.class, name);

    return new TestFunction(
        testIdentifier,
        PointOfInterestTests.TEST_ENVIRONMENT_DEFAULT,
        PointOfInterestTests.TEST_STRUCTURE_EMPTY,
        100,
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

          context.setBlock(blockPos.south(2).above(1), Blocks.BARRIER);
          context.setBlock(blockPos.south(1).east(1).above(1), Blocks.BARRIER);
          context.setBlock(blockPos.south(1).above(2), Blocks.BARRIER);

          Villager villager = (Villager) context.spawn(EntityType.VILLAGER, blockPos.south(1));

          // Act
          CompletableFuture<Void> futurePartialAct1 = new CompletableFuture<>();
          CompletableFuture<Void> futurePartialAct2 = new CompletableFuture<>();

          long tickOrigin = 0;
          context.runAtTickTime(
              tickOrigin,
              () -> {
                context.setBlock(blockPos, barrelBlock);

                futurePartialAct1.complete(null);
              });

          long tickGetProfession = 100;
          context.runAtTickTime(
              tickGetProfession,
              () -> {
                futurePartialAct2.complete(null);
              });

          // Assert
          CompletableFuture.allOf(futurePartialAct1, futurePartialAct2)
              .thenRun(
                  () -> {
                    try {
                      context.assertValueEqual(
                          villager.getVillagerData().profession().unwrapKey().orElse(null),
                          VillagerProfession.FISHERMAN,
                          Component.literal("villager profession"));
                    } catch (Exception e) {
                      ReinforcedBarrelsMod.LOGGER.error("[{}] {}", testIdentifier, e.getMessage());
                      throw e;
                    }

                    context.succeed();
                  });
        });
  }
}
