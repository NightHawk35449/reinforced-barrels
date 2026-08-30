package atonkish.reinfbarrel.world.poi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import atonkish.reinfbarrel.block.ModBlocks;
import atonkish.reinfbarrel.mixin.PointOfInterestTypesAccessor;

// SPECULATIVE - NOT CONFIRMED against the actual 26.1.2 jar. See the caveat in
// PointOfInterestTypesAccessor.java: neither reinforced-core nor reinforced-chests touches POI
// code, so I have no decompiled evidence for PoiType/PoiTypes' real 26.1.2 shape, field names,
// or even whether "FISHERMAN" is still the right constant name. This file compiles the same
// logic as the 1.21 version with names updated to plausible (but unverified) Mojang mappings.
// Confidence: Speculative / Risk: High.
public class ModPointOfInterestTypes {
  public static void init() {
    Map<BlockState, Holder<PoiType>> poiStatesToType =
        PointOfInterestTypesAccessor.getPointOfInterestStatesToType();

    PoiType fishermanPoiType =
        BuiltInRegistries.POINT_OF_INTEREST_TYPE.getValue(PoiTypes.FISHERMAN);

    Holder<PoiType> fishermanEntry =
        BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(fishermanPoiType);

    // NOTE: PoiType's block-state set field is accessible by access widener (see
    // reinfbarrel.accesswidener) - field name there is also unverified.
    List<BlockState> fishermanBlockStates = new ArrayList<>(fishermanPoiType.matchingStates);

    for (Block block : ModBlocks.REINFORCED_BARREL_MAP.values()) {
      ImmutableList<BlockState> blockStates = block.getStateDefinition().getPossibleStates();

      for (BlockState blockState : blockStates) {
        poiStatesToType.putIfAbsent(blockState, fishermanEntry);
      }

      fishermanBlockStates.addAll(blockStates);
    }

    // NOTE: mutable by access widener.
    fishermanPoiType.matchingStates = ImmutableSet.copyOf(fishermanBlockStates);
  }
}
