package atonkish.reinfbarrel.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import atonkish.reinfcore.api.ReinforcedCoreRegistry;
import atonkish.reinfcore.util.ReinforcingMaterial;

// CONFIRMED against decompiled reinforced-chests 26.1.2 ReinforcingMaterialSettings.java
// (identical pattern): AbstractBlock.Settings -> BlockBehaviour.Properties,
// .create() -> .of(), BlockSoundGroup -> SoundType, .sounds(...) -> .sound(...),
// MapColor.ORANGE/IRON_GRAY/BLACK -> MapColor.COLOR_ORANGE/METAL/COLOR_BLACK,
// Item.Settings -> Item.Properties, .fireproof() -> .fireResistant().
public enum ReinforcingMaterialSettings {
  COPPER(
      ReinforcedCoreRegistry.registerReinforcingMaterial("copper", 45, Items.COPPER_INGOT),
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.COLOR_ORANGE)
          .strength(2.5F, 6.0F)
          .sound(SoundType.COPPER),
      new Item.Properties()),
  IRON(
      ReinforcedCoreRegistry.registerReinforcingMaterial("iron", 54, Items.IRON_INGOT),
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
          .strength(2.5F, 6.0F)
          .sound(SoundType.METAL),
      new Item.Properties()),
  GOLD(
      ReinforcedCoreRegistry.registerReinforcingMaterial("gold", 81, Items.GOLD_INGOT),
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.GOLD)
          .instrument(NoteBlockInstrument.BELL)
          .strength(2.5F, 6.0F)
          .sound(SoundType.METAL),
      new Item.Properties()),
  DIAMOND(
      ReinforcedCoreRegistry.registerReinforcingMaterial("diamond", 108, Items.DIAMOND),
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.DIAMOND)
          .strength(2.5F, 6.0F)
          .sound(SoundType.METAL),
      new Item.Properties()),
  NETHERITE(
      ReinforcedCoreRegistry.registerReinforcingMaterial("netherite", 108, Items.NETHERITE_INGOT),
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.COLOR_BLACK)
          .strength(2.5F, 1200.0F)
          .sound(SoundType.NETHERITE_BLOCK),
      new Item.Properties().fireResistant());

  private final ReinforcingMaterial material;
  private final BlockBehaviour.Properties blockSettings;
  private final Item.Properties itemSettings;

  private ReinforcingMaterialSettings(
      ReinforcingMaterial material,
      BlockBehaviour.Properties blockSettings,
      Item.Properties itemSettings) {
    this.material = material;
    this.blockSettings = blockSettings;
    this.itemSettings = itemSettings;
  }

  public ReinforcingMaterial getMaterial() {
    return this.material;
  }

  public BlockBehaviour.Properties getBlockSettings() {
    return this.blockSettings;
  }

  public Item.Properties getItemSettings() {
    return this.itemSettings;
  }
}
