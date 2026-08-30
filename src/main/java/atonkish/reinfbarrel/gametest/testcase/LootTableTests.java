package atonkish.reinfbarrel.gametest.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

import atonkish.reinfbarrel.ReinforcedBarrelsMod;
import atonkish.reinfbarrel.block.ModBlocks;
import atonkish.reinfbarrel.gametest.util.MockServerPlayerHelper;
import atonkish.reinfbarrel.gametest.util.TestIdentifier;
import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.util.ReinforcingMaterials;

// CONFIRMED against decompiled reinforced-chests 26.1.2 LootTableTests.java (identical
// pattern): PlayerActionC2SPacket -> ServerboundPlayerActionPacket (package
// net.minecraft.network.packet.c2s.play -> net.minecraft.network.protocol.game), Hand ->
// InteractionHand, interactionManager.processBlockBreakingAction -> gameMode
// .handleBlockBreakAction, context.getAbsolutePos -> context.absolutePos, calcBlockBreakingDelta
// -> getDestroyProgress (arg order: player, level, pos), context.expectBlock ->
// context.assertBlockPresent, context.expectItemsAt -> context.assertItemEntityCountIs,
// context.complete() -> context.succeed(), plus all the previously-confirmed renames
// (BlockRotation->Rotation, ServerPlayerEntity->ServerPlayer, GameMode->GameType, etc).
public class LootTableTests {
  private static final String TEST_ENVIRONMENT_DEFAULT =
      String.format("%s:loot_table/default", ReinforcedBarrelsMod.MOD_ID);
  private static final String TEST_STRUCTURE_EMPTY = "fabric-gametest-api-v1:empty";

  public static final Collection<TestFunction> TEST_FUNCTIONS =
      new ArrayList<>() {
        {
          // Copper Barrel
          add(
              LootTableTests.createTest(
                  "Break Copper Barrel with Netherite Axe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper")),
                  Items.NETHERITE_AXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Copper Barrel with Netherite Pickaxe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper")),
                  Items.NETHERITE_PICKAXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Copper Barrel without tools",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper")),
                  Items.AIR,
                  true));

          // Iron Barrel
          add(
              LootTableTests.createTest(
                  "Break Iron Barrel with Netherite Axe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron")),
                  Items.NETHERITE_AXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Iron Barrel with Netherite Pickaxe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron")),
                  Items.NETHERITE_PICKAXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Iron Barrel without tools",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron")),
                  Items.AIR,
                  true));

          // Gold Barrel
          add(
              LootTableTests.createTest(
                  "Break Gold Barrel with Netherite Axe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold")),
                  Items.NETHERITE_AXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Gold Barrel with Netherite Pickaxe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold")),
                  Items.NETHERITE_PICKAXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Gold Barrel without tools",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold")),
                  Items.AIR,
                  true));

          // Diamond Barrel
          add(
              LootTableTests.createTest(
                  "Break Diamond Barrel with Netherite Axe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond")),
                  Items.NETHERITE_AXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Diamond Barrel with Netherite Pickaxe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond")),
                  Items.NETHERITE_PICKAXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Diamond Barrel without tools",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond")),
                  Items.AIR,
                  true));

          // Netherite Barrel
          add(
              LootTableTests.createTest(
                  "Break Netherite Barrel with Netherite Axe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("netherite")),
                  Items.NETHERITE_AXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Netherite Barrel with Netherite Pickaxe",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("netherite")),
                  Items.NETHERITE_PICKAXE,
                  true));
          add(
              LootTableTests.createTest(
                  "Break Netherite Barrel without tools",
                  ModBlocks.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("netherite")),
                  Items.AIR,
                  true));
        }
      };

  private static TestFunction createTest(
      String name, Block barrelBlock, Item tool, boolean shouldDrop) {
    Identifier testIdentifier =
        TestIdentifier.of(ReinforcedBarrelsMod.MOD_ID, LootTableTests.class, name);

    return new TestFunction(
        testIdentifier,
        LootTableTests.TEST_ENVIRONMENT_DEFAULT,
        LootTableTests.TEST_STRUCTURE_EMPTY,
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
          player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack((ItemLike) tool));

          // Act
          CompletableFuture<Void> futurePartialAct1 = new CompletableFuture<>();
          CompletableFuture<Void> futurePartialAct2 = new CompletableFuture<>();

          long tickOrigin = 0;
          context.runAtTickTime(
              tickOrigin,
              () -> {
                player.gameMode.handleBlockBreakAction(
                    context.absolutePos(blockPos),
                    ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                    Direction.NORTH,
                    context.getLevel().getHeight(),
                    0);

                futurePartialAct1.complete(null);
              });

          long tickBlockBreaking =
              (long)
                  Math.ceil(
                      1.0D
                          / context
                              .getBlockState(blockPos)
                              .getDestroyProgress(
                                  player, (BlockGetter) context.getLevel(), blockPos));
          context.runAtTickTime(
              tickBlockBreaking,
              () -> {
                player.gameMode.handleBlockBreakAction(
                    context.absolutePos(blockPos),
                    ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                    Direction.NORTH,
                    context.getLevel().getHeight(),
                    0);

                futurePartialAct2.complete(null);
              });

          ReinforcedBarrelsMod.LOGGER.info(
              "[{}] {} can be mined in {} ticks by {}",
              testIdentifier,
              Component.translatable(barrelBlock.getDescriptionId()).getString(),
              tickBlockBreaking,
              Component.translatable(tool.getDescriptionId()).getString());

          // Assert
          CompletableFuture.allOf(futurePartialAct1, futurePartialAct2)
              .thenRun(
                  () -> {
                    try {
                      context.assertBlockPresent(Blocks.AIR, blockPos);
                      context.assertItemEntityCountIs(
                          barrelBlock.asItem(), blockPos, 1.0, shouldDrop ? 1 : 0);
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
