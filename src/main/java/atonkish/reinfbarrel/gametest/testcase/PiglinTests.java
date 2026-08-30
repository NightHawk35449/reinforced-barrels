package atonkish.reinfbarrel.gametest.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

import atonkish.reinfbarrel.ReinforcedBarrelsMod;
import atonkish.reinfbarrel.block.ModBlocks;
import atonkish.reinfbarrel.gametest.util.MockServerPlayerHelper;
import atonkish.reinfbarrel.gametest.util.TestIdentifier;
import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.util.ReinforcingMaterials;

// CONFIRMED against decompiled reinforced-chests 26.1.2 PiglinTests.java for the package/method
// renames used throughout this file (EntityType/EquipmentSlot -> net.minecraft.world.entity.*,
// MemoryModuleType -> net.minecraft.world.entity.ai.memory (not .ai.brain), PiglinEntity ->
// Piglin (package net.minecraft.world.entity.monster.piglin), context.spawnMob ->
// context.spawn, equipStack -> setItemSlot, hasMemoryModuleWithValue -> isMemoryValue,
// getUuid() -> getUUID()). Note: unlike Chest, the original 1.21 ReinforcedBarrelBlock.java
// makes its own explicit PiglinBrain.onGuardedBlockInteracted(...) call - that call has been
// restored in the ported ReinforcedBarrelBlock (renamed to angerNearbyPiglins) since it's real
// original Barrel behavior, not something to drop just because Chest doesn't need it.
public class PiglinTests {
  private static final String TEST_ENVIRONMENT_DEFAULT =
      String.format("%s:piglin/default", ReinforcedBarrelsMod.MOD_ID);
  private static final String TEST_STRUCTURE_EMPTY = "fabric-gametest-api-v1:empty";

  public static final Collection<TestFunction> TEST_FUNCTIONS =
      new ArrayList<>() {
        {
          // Copper Barrel
          add(
              PiglinTests.createTest(
                  "Piglin get angry after opening Copper Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper"))));

          // Iron Barrel
          add(
              PiglinTests.createTest(
                  "Piglin get angry after opening Iron Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron"))));

          // Gold Barrel
          add(
              PiglinTests.createTest(
                  "Piglin get angry after opening Gold Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold"))));

          // Diamond Barrel
          add(
              PiglinTests.createTest(
                  "Piglin get angry after opening Diamond Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond"))));

          // Netherite Barrel
          add(
              PiglinTests.createTest(
                  "Piglin get angry after opening Netherite Barrel",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("netherite"))));
        }
      };

  private static TestFunction createTest(String name, Block barrelBlock) {
    Identifier testIdentifier =
        TestIdentifier.of(ReinforcedBarrelsMod.MOD_ID, PiglinTests.class, name);

    return new TestFunction(
        testIdentifier,
        PiglinTests.TEST_ENVIRONMENT_DEFAULT,
        PiglinTests.TEST_STRUCTURE_EMPTY,
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
          context.setBlock(blockPos, barrelBlock);

          ServerPlayer player =
              MockServerPlayerHelper.spawn(
                  context, GameType.SURVIVAL, Vec3.atCenterOf((Vec3i) blockPos.south(4)));
          player.setItemSlot(
              EquipmentSlot.CHEST, new ItemStack((ItemLike) Items.GOLDEN_CHESTPLATE));

          Piglin piglin = (Piglin) context.spawn(EntityType.PIGLIN, blockPos.east(1));

          // Act
          CompletableFuture<Void> futurePartialAct1 = new CompletableFuture<>();
          CompletableFuture<Void> futurePartialAct2 = new CompletableFuture<>();

          Map<String, Boolean> angryAtMap = new HashMap<String, Boolean>();
          String angryAtMapKeyBeforeAngryAtPlayer = "beforeAngryAtPlayer";
          String angryAtMapKeyAfterAngryAtPlayer = "afterAngryAtPlayer";

          long tickBarrelOpen = 20;
          context.runAtTickTime(
              tickBarrelOpen,
              () -> {
                angryAtMap.put(
                    angryAtMapKeyBeforeAngryAtPlayer,
                    piglin.getBrain().isMemoryValue(MemoryModuleType.ANGRY_AT, player.getUUID()));

                context.useBlock(blockPos, (Player) player);

                futurePartialAct1.complete(null);
              });

          long tickAngryAtPlayer = 21;
          context.runAtTickTime(
              tickAngryAtPlayer,
              () -> {
                angryAtMap.put(
                    angryAtMapKeyAfterAngryAtPlayer,
                    piglin.getBrain().isMemoryValue(MemoryModuleType.ANGRY_AT, player.getUUID()));

                futurePartialAct2.complete(null);
              });

          // Assert
          CompletableFuture.allOf(futurePartialAct1, futurePartialAct2)
              .thenRun(
                  () -> {
                    try {
                      context.assertFalse(
                          angryAtMap.get(angryAtMapKeyBeforeAngryAtPlayer),
                          Component.literal(
                              "Expected that the piglin is not angry at player, but it has been already angry."));
                      context.assertTrue(
                          angryAtMap.get(angryAtMapKeyAfterAngryAtPlayer),
                          Component.literal(
                              "Expected that the piglin is angry at player, but it has not been angry yet."));
                    } catch (Exception e) {
                      ReinforcedBarrelsMod.LOGGER.error("[{}] {}", testIdentifier, e.getMessage());
                      throw e;
                    } finally {
                      MockServerPlayerHelper.destroy(context, player);
                    }

                    context.succeed();
                  });
        });
  }
}
