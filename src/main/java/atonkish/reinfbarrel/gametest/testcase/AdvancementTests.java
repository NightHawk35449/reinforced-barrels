package atonkish.reinfbarrel.gametest.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

import atonkish.reinfbarrel.ReinforcedBarrelsMod;
import atonkish.reinfbarrel.gametest.util.MockServerPlayerHelper;
import atonkish.reinfbarrel.gametest.util.TestIdentifier;
import atonkish.reinfbarrel.item.ModItems;
import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.util.ReinforcingMaterials;

// CONFIRMED against decompiled reinforced-chests 26.1.2 AdvancementTests.java (identical
// pattern): AdvancementEntry -> AdvancementHolder (package net.minecraft.advancement ->
// net.minecraft.advancements), Item/ItemStack/Items -> net.minecraft.world.item.*,
// ServerPlayerEntity -> ServerPlayer, Text -> Component, BlockRotation -> Rotation
// (net.minecraft.world.level.block), Identifier.of -> Identifier.fromNamespaceAndPath,
// BlockPos/Vec3d -> net.minecraft.core.BlockPos / net.minecraft.world.phys.Vec3 (Vec3d.of(pos)
// -> Vec3.atCenterOf(pos)), GameMode -> GameType, context.getWorld() -> context.getLevel(),
// getAdvancementLoader() -> getAdvancements(), getAdvancementTracker().getProgress(...) ->
// getAdvancements().getOrStartProgress(...), player.giveItemStack(...) ->
// player.getInventory().add(...), context.runAtTick -> context.runAtTickTime,
// context.complete() -> context.succeed().
public class AdvancementTests {
  private static final String TEST_ENVIRONMENT_DEFAULT =
      String.format("%s:advancement/default", ReinforcedBarrelsMod.MOD_ID);
  private static final String TEST_STRUCTURE_EMPTY = "fabric-gametest-api-v1:empty";

  public static final Collection<TestFunction> TEST_FUNCTIONS =
      new ArrayList<>() {
        {
          // Copper Barrel
          add(
              AdvancementTests.createTest(
                  "Obtain Copper Barrel recipe advancement by having Barrel",
                  Items.BARREL,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID, "recipes/decorations/copper_barrel")));
          add(
              AdvancementTests.createTest(
                  "Obtain Copper Barrel recipe advancement by having Copper Ingot",
                  Items.COPPER_INGOT,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID, "recipes/decorations/copper_barrel")));

          // Iron Barrel
          add(
              AdvancementTests.createTest(
                  "Obtain Iron Barrel recipe advancement by having Copper Barrel",
                  ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper")),
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID, "recipes/decorations/iron_barrel")));
          add(
              AdvancementTests.createTest(
                  "Obtain Iron Barrel recipe advancement by having Iron Ingot",
                  Items.IRON_INGOT,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID, "recipes/decorations/iron_barrel")));

          // Gold Barrel
          add(
              AdvancementTests.createTest(
                  "Obtain Gold Barrel recipe advancement by having Iron Barrel",
                  ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron")),
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID, "recipes/decorations/gold_barrel")));
          add(
              AdvancementTests.createTest(
                  "Obtain Gold Barrel recipe advancement by having Gold Ingot",
                  Items.GOLD_INGOT,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID, "recipes/decorations/gold_barrel")));

          // Diamond Barrel
          add(
              AdvancementTests.createTest(
                  "Obtain Diamond Barrel recipe advancement by having Gold Barrel",
                  ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold")),
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID, "recipes/decorations/diamond_barrel")));
          add(
              AdvancementTests.createTest(
                  "Obtain Diamond Barrel recipe advancement by having Diamond Ingot",
                  Items.DIAMOND,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID, "recipes/decorations/diamond_barrel")));

          // Netherite Barrel
          add(
              AdvancementTests.createTest(
                  "Obtain Netherite Barrel recipe advancement by having Diamond Barrel",
                  ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond")),
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID,
                      "recipes/decorations/netherite_barrel_smithing")));
          add(
              AdvancementTests.createTest(
                  "Obtain Netherite Barrel recipe advancement by having Netherite Ingot",
                  Items.NETHERITE_INGOT,
                  Identifier.fromNamespaceAndPath(
                      ReinforcedBarrelsMod.MOD_ID,
                      "recipes/decorations/netherite_barrel_smithing")));
        }
      };

  private static TestFunction createTest(String name, Item item, Identifier advancementId) {
    Identifier testIdentifier =
        TestIdentifier.of(ReinforcedBarrelsMod.MOD_ID, AdvancementTests.class, name);

    return new TestFunction(
        testIdentifier,
        AdvancementTests.TEST_ENVIRONMENT_DEFAULT,
        AdvancementTests.TEST_STRUCTURE_EMPTY,
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
          ServerPlayer player =
              MockServerPlayerHelper.spawn(
                  context, GameType.SURVIVAL, Vec3.atCenterOf((Vec3i) BlockPos.ZERO));
          AdvancementHolder entry =
              context.getLevel().getServer().getAdvancements().get(advancementId);
          AdvancementProgress progress = player.getAdvancements().getOrStartProgress(entry);

          // Act
          CompletableFuture<Void> futurePartialAct1 = new CompletableFuture<>();
          CompletableFuture<Void> futurePartialAct2 = new CompletableFuture<>();

          Map<String, Boolean> progressMap = new HashMap<String, Boolean>();
          String progressMapKeyBeforeHavingItem = "beforeHavingItem";
          String progressMapKeyAfterHavingItem = "afterHavingItem";

          long tickOrigin = 0;
          context.runAtTickTime(
              tickOrigin,
              () -> {
                progressMap.put(progressMapKeyBeforeHavingItem, progress.isDone());

                player.getInventory().add(new ItemStack((ItemLike) item));

                futurePartialAct1.complete(null);
              });

          long tickObtained = 1;
          context.runAtTickTime(
              tickObtained,
              () -> {
                progressMap.put(progressMapKeyAfterHavingItem, progress.isDone());

                futurePartialAct2.complete(null);
              });

          // Assert
          CompletableFuture.allOf(futurePartialAct1, futurePartialAct2)
              .thenRun(
                  () -> {
                    try {
                      context.assertFalse(
                          progressMap.get(progressMapKeyBeforeHavingItem),
                          Component.literal(
                              String.format(
                                  "Expected that advancement %s has not been done yet, but it has been already done.",
                                  entry)));
                      context.assertTrue(
                          progressMap.get(progressMapKeyAfterHavingItem),
                          Component.literal(
                              String.format(
                                  "Expected that advancement %s has been done, but it has not been done yet.",
                                  entry)));
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
