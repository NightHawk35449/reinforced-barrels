package atonkish.reinfbarrel.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import atonkish.reinfbarrel.block.ModBlocks;
import atonkish.reinfbarrel.block.entity.ModBlockEntityType;
import atonkish.reinfbarrel.block.entity.ReinforcedBarrelBlockEntity;
import atonkish.reinfbarrel.item.ModItems;
import atonkish.reinfbarrel.stat.ModStats;
import atonkish.reinfcore.util.ReinforcingMaterial;

// CONFIRMED against decompiled reinforced-chests 26.1.2 ReinforcedChestsRegistry.java
// (identical shape): Block.Settings -> BlockBehaviour.Properties, Item.Settings -> Item.Properties,
// package moves for Block/BlockEntityType/Item/Identifier.
public class ReinforcedBarrelsRegistry {
  public static Identifier registerMaterialOpenStat(
      String namespace, ReinforcingMaterial material) {
    return ModStats.registerMaterialOpen(namespace, material);
  }

  public static Block registerMaterialBlock(
      String namespace, ReinforcingMaterial material, BlockBehaviour.Properties settings) {
    return ModBlocks.registerMaterial(namespace, material, settings);
  }

  public static BlockEntityType<ReinforcedBarrelBlockEntity> registerMaterialBlockEntityType(
      String namespace, ReinforcingMaterial material) {
    return ModBlockEntityType.registerMaterial(namespace, material);
  }

  public static Item registerMaterialItem(
      String namespace, ReinforcingMaterial material, Item.Properties settings) {
    return ModItems.registerMaterial(material, settings);
  }

  public static void registerMaterialItemGroupIcon(String namespace, ReinforcingMaterial material) {
    ModItems.registerMaterialItemGroupIcon(material);
  }
}
