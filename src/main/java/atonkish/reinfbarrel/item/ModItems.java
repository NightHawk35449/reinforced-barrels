package atonkish.reinfbarrel.item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

import atonkish.reinfbarrel.block.ModBlocks;
import atonkish.reinfcore.item.ModItemGroup;
import atonkish.reinfcore.item.ModItemGroups;
import atonkish.reinfcore.util.ReinforcingMaterial;

// CONFIRMED against decompiled reinforced-chests 26.1.2 ModItems.java (identical pattern):
// ItemGroupEvents.modifyEntriesEvent -> CreativeModeTabEvents.modifyOutputEvent,
// ItemGroups.FUNCTIONAL/REDSTONE -> CreativeModeTabs.FUNCTIONAL_BLOCKS/REDSTONE_BLOCKS,
// content.add(item) -> output.accept(item), Item.Settings -> Item.Properties,
// settings.useBlockPrefixedTranslationKey() -> settings.useBlockDescriptionPrefix(),
// settings.registryKey(key) -> settings.setId(key), blockItem.appendBlocks(...) ->
// blockItem.registerBlocks(Item.BY_BLOCK, item), block.getRegistryEntry().registryKey() ->
// block.builtInRegistryHolder().key(), RegistryKey.getValue() -> ResourceKey.identifier().
public class ModItems {
  public static final Map<ReinforcingMaterial, Item> REINFORCED_BARREL_MAP = new LinkedHashMap<>();
  public static final Map<ReinforcingMaterial, Item.Properties> REINFORCED_BARREL_SETTINGS_MAP =
      new LinkedHashMap<>();

  public static Item registerMaterial(ReinforcingMaterial material, Item.Properties settings) {
    if (!REINFORCED_BARREL_SETTINGS_MAP.containsKey(material)) {
      REINFORCED_BARREL_SETTINGS_MAP.put(material, settings);
    }

    if (!REINFORCED_BARREL_MAP.containsKey(material)) {
      Item item =
          ModItems.register(
              ModBlocks.REINFORCED_BARREL_MAP.get(material),
              REINFORCED_BARREL_SETTINGS_MAP.get(material));
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
          .register(output -> output.accept(item));
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS)
          .register(output -> output.accept(item));
      CreativeModeTabEvents.modifyOutputEvent(ModItemGroups.REINFORCED_STORAGE)
          .register(output -> output.accept(item));
      REINFORCED_BARREL_MAP.put(material, item);
    }

    return REINFORCED_BARREL_MAP.get(material);
  }

  public static void registerMaterialItemGroupIcon(ReinforcingMaterial material) {
    Item item = REINFORCED_BARREL_MAP.get(material);
    CreativeModeTabEvents.modifyOutputEvent(ModItemGroups.REINFORCED_STORAGE)
        .register(
            output ->
                ModItemGroup.setIcon(
                    BuiltInRegistries.CREATIVE_MODE_TAB.getValue(ModItemGroups.REINFORCED_STORAGE),
                    item));
  }

  private static ResourceKey<Item> keyOf(ResourceKey<Block> blockKey) {
    return ResourceKey.create(Registries.ITEM, blockKey.identifier());
  }

  public static Item register(Block block, Item.Properties settings) {
    return register(block, BlockItem::new, settings);
  }

  private static Item register(
      Block block, BiFunction<Block, Item.Properties, Item> factory, Item.Properties settings) {
    return register(
        keyOf(block.builtInRegistryHolder().key()),
        itemSettings -> factory.apply(block, itemSettings),
        settings.useBlockDescriptionPrefix());
  }

  private static Item register(
      ResourceKey<Item> key, Function<Item.Properties, Item> factory, Item.Properties settings) {
    Item item = factory.apply(settings.setId(key));
    if (item instanceof BlockItem blockItem) {
      blockItem.registerBlocks(Item.BY_BLOCK, item);
    }

    return Registry.register(BuiltInRegistries.ITEM, key, item);
  }
}
