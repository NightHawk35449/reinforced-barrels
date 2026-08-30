package atonkish.reinfbarrel.block.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import atonkish.reinfbarrel.block.ModBlocks;
import atonkish.reinfbarrel.mixin.BlockEntityTypeAccessor;
import atonkish.reinfcore.util.ReinforcingMaterial;

// CONFIRMED against decompiled reinforced-chests 26.1.2 ModBlockEntityType.java: the old
// BlockEntityTypeInvoker (@Invoker into the protected BlockEntityType.create(...) method) is
// gone entirely, replaced by Fabric API's FabricBlockEntityTypeBuilder.create(factory, blocks)
// .build(). BlockEntityType.CHEST's accessor is getValidBlocks() (renamed from getBlocks()),
// used identically here for BlockEntityType.BARREL. Evidence: High confidence / Risk: Low.
public class ModBlockEntityType {
  public static final Map<ReinforcingMaterial, BlockEntityType<ReinforcedBarrelBlockEntity>>
      REINFORCED_BARREL_MAP = new LinkedHashMap<>();

  public static BlockEntityType<ReinforcedBarrelBlockEntity> registerMaterial(
      String namespace, ReinforcingMaterial material) {
    if (!REINFORCED_BARREL_MAP.containsKey(material)) {
      String id = material.getName() + "_barrel";
      Block block = ModBlocks.REINFORCED_BARREL_MAP.get(material);
      Identifier identifier = Identifier.fromNamespaceAndPath(namespace, id);
      BlockEntityType<ReinforcedBarrelBlockEntity> blockEntityType =
          Registry.register(
              BuiltInRegistries.BLOCK_ENTITY_TYPE,
              identifier,
              FabricBlockEntityTypeBuilder.create(
                      (blockPos, blockState) ->
                          new ReinforcedBarrelBlockEntity(material, blockPos, blockState),
                      block)
                  .build());
      REINFORCED_BARREL_MAP.put(material, blockEntityType);

      ((BlockEntityTypeAccessor) BlockEntityType.BARREL).getValidBlocks().add(block);
    }

    return REINFORCED_BARREL_MAP.get(material);
  }
}
