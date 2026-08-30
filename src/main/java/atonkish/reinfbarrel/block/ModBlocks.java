package atonkish.reinfbarrel.block;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import atonkish.reinfcore.util.ReinforcingMaterial;

// CONFIRMED against decompiled reinforced-chests 26.1.2 ModBlocks.java (identical
// registration shape for chests): package moves, RegistryKey -> ResourceKey,
// Block.Settings -> BlockBehaviour.Properties, Identifier.of -> Identifier.fromNamespaceAndPath,
// settings.registryKey(key) -> settings.setId(key), Registry.register uses BuiltInRegistries.BLOCK.
// Evidence: High confidence / Risk: Low.
public class ModBlocks {
  public static final Map<ReinforcingMaterial, Block> REINFORCED_BARREL_MAP = new LinkedHashMap<>();
  public static final Map<ReinforcingMaterial, BlockBehaviour.Properties>
      REINFORCED_BARREL_SETTINGS_MAP = new LinkedHashMap<>();

  public static Block registerMaterial(
      String namespace, ReinforcingMaterial material, BlockBehaviour.Properties settings) {
    if (!REINFORCED_BARREL_SETTINGS_MAP.containsKey(material)) {
      REINFORCED_BARREL_SETTINGS_MAP.put(material, settings);
    }

    if (!REINFORCED_BARREL_MAP.containsKey(material)) {
      Block block =
          ModBlocks.register(
              Identifier.fromNamespaceAndPath(namespace, material.getName() + "_barrel"),
              (blockProperties) -> new ReinforcedBarrelBlock(material, blockProperties),
              REINFORCED_BARREL_SETTINGS_MAP.get(material));
      REINFORCED_BARREL_MAP.put(material, block);
    }

    return REINFORCED_BARREL_MAP.get(material);
  }

  private static Block register(
      ResourceKey<Block> key,
      Function<BlockBehaviour.Properties, Block> factory,
      BlockBehaviour.Properties settings) {
    Block block = factory.apply(settings.setId(key));
    return Registry.register(BuiltInRegistries.BLOCK, key, block);
  }

  private static Block register(
      Identifier id,
      Function<BlockBehaviour.Properties, Block> factory,
      BlockBehaviour.Properties settings) {
    return register(keyOf(id), factory, settings);
  }

  private static ResourceKey<Block> keyOf(Identifier id) {
    return ResourceKey.create(Registries.BLOCK, id);
  }
}
