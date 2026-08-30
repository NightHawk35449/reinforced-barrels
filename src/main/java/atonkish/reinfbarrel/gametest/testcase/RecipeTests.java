package atonkish.reinfbarrel.gametest.testcase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

import atonkish.reinfbarrel.ReinforcedBarrelsMod;
import atonkish.reinfbarrel.gametest.util.TestIdentifier;
import atonkish.reinfbarrel.item.ModItems;
import atonkish.reinfcore.gametest.TestFunction;
import atonkish.reinfcore.util.ReinforcingMaterials;

// CONFIRMED against decompiled reinforced-chests 26.1.2 RecipeTests.java (identical pattern):
// CraftingRecipeInput -> CraftingInput (.create(...) -> .of(...)), Recipe/RecipeInput/
// RecipeType/SmithingRecipeInput -> net.minecraft.world.item.crafting.*, ServerRecipeManager
// -> RecipeManager, DynamicRegistryManager -> RegistryAccess (net.minecraft.core), ServerWorld
// -> ServerLevel, world.getRegistryManager() -> world.registryAccess(),
// recipeManager.getFirstMatch(type,input,world) -> recipeManager.getRecipeFor(type,input,world)
// (returns Optional<RecipeHolder<T>>, unwrap via .orElseThrow().value()),
// recipe.craft(input, registryManager) -> recipe.assemble(input) (single arg only -
// RegistryAccess is no longer passed to assemble), ItemStack.areEqual -> ItemStack.matches.
public class RecipeTests {
  private static final String TEST_ENVIRONMENT_DEFAULT =
      String.format("%s:recipe/default", ReinforcedBarrelsMod.MOD_ID);
  private static final String TEST_STRUCTURE_EMPTY = "fabric-gametest-api-v1:empty";

  public static final Collection<TestFunction> TEST_FUNCTIONS =
      new ArrayList<>() {
        {
          // Copper Barrel
          {
            ItemStack baseBarrel = new ItemStack(Items.BARREL);
            ItemStack material = new ItemStack(Items.COPPER_INGOT);
            ItemStack barrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper")));

            add(
                RecipeTests.createTest(
                    "Craft Copper Barrel",
                    RecipeType.CRAFTING,
                    CraftingInput.of(
                        3,
                        3,
                        List.of(
                            material,
                            material,
                            material,
                            material,
                            baseBarrel,
                            material,
                            material,
                            material,
                            material)),
                    barrel));
          }

          // Iron Barrel
          {
            ItemStack baseBarrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("copper")));
            ItemStack material = new ItemStack(Items.IRON_INGOT);
            ItemStack barrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron")));

            add(
                RecipeTests.createTest(
                    "Craft Iron Barrel",
                    RecipeType.CRAFTING,
                    CraftingInput.of(
                        3,
                        3,
                        List.of(
                            material,
                            material,
                            material,
                            material,
                            baseBarrel,
                            material,
                            material,
                            material,
                            material)),
                    barrel));
          }

          // Gold Barrel
          {
            ItemStack baseBarrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("iron")));
            ItemStack material = new ItemStack(Items.GOLD_INGOT);
            ItemStack barrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold")));

            add(
                RecipeTests.createTest(
                    "Craft Gold Barrel",
                    RecipeType.CRAFTING,
                    CraftingInput.of(
                        3,
                        3,
                        List.of(
                            material,
                            material,
                            material,
                            material,
                            baseBarrel,
                            material,
                            material,
                            material,
                            material)),
                    barrel));
          }

          // Diamond Barrel
          {
            ItemStack baseBarrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("gold")));
            ItemStack material = new ItemStack(Items.DIAMOND);
            ItemStack barrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond")));

            add(
                RecipeTests.createTest(
                    "Craft Diamond Barrel",
                    RecipeType.CRAFTING,
                    CraftingInput.of(
                        3,
                        3,
                        List.of(
                            material,
                            material,
                            material,
                            material,
                            baseBarrel,
                            material,
                            material,
                            material,
                            material)),
                    barrel));
          }

          // Netherite Barrel
          {
            ItemStack template = new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
            ItemStack baseBarrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("diamond")));
            ItemStack material = new ItemStack(Items.NETHERITE_INGOT);
            ItemStack barrel =
                new ItemStack(
                    ModItems.REINFORCED_BARREL_MAP.get(ReinforcingMaterials.MAP.get("netherite")));

            add(
                RecipeTests.createTest(
                    "Smithing Netherite Barrel",
                    RecipeType.SMITHING,
                    new SmithingRecipeInput(template, baseBarrel, material),
                    barrel));
          }
        }
      };

  private static <I extends RecipeInput, T extends Recipe<I>> TestFunction createTest(
      String name, RecipeType<T> type, I input, ItemStack expected) {
    Identifier testIdentifier =
        TestIdentifier.of(ReinforcedBarrelsMod.MOD_ID, RecipeTests.class, name);

    return new TestFunction(
        testIdentifier,
        RecipeTests.TEST_ENVIRONMENT_DEFAULT,
        RecipeTests.TEST_STRUCTURE_EMPTY,
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
          ServerLevel world = context.getLevel();
          RecipeManager recipeManager = world.getServer().getRecipeManager();
          RegistryAccess registryManager = world.registryAccess();
          @SuppressWarnings("unchecked")
          T recipe =
              ((RecipeHolder<T>)
                      recipeManager.getRecipeFor(type, input, (Level) world).orElseThrow())
                  .value();

          // Act
          ItemStack actual = recipe.assemble(input);

          // Assert
          try {
            context.assertTrue(
                ItemStack.matches(actual, expected),
                Component.literal("Recipe result differs from expected."));
          } catch (Exception e) {
            ReinforcedBarrelsMod.LOGGER.error("[{}] {}", testIdentifier, e.getMessage());
            throw e;
          }

          context.succeed();
        });
  }
}
