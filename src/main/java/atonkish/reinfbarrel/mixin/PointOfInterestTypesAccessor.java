package atonkish.reinfbarrel.mixin;

import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// SPECULATIVE - NOT CONFIRMED against the actual 26.1.2 jar.
// Neither reinforced-core nor reinforced-chests touches POI (point-of-interest) code, so I
// had no decompiled 26.1.2 evidence to check this against (chests aren't a vanilla POI block;
// only barrels are - they're the "fisherman" POI). Everything below is carried over from the
// 1.21 mixin (which targeted net.minecraft.world.poi.PointOfInterestTypes /
// POI_STATES_TO_TYPE) with class/package names updated to match real, historical Mojang
// mapping names for this class (net.minecraft.world.entity.ai.village.poi.PoiType/PoiTypes),
// which is the pattern used before Minecraft's obfuscation was removed - it is a reasonable
// starting guess, not a verified fact for 26.1.2 specifically.
// Before relying on this: run genSources locally and open the real PoiType/PoiTypes classes,
// or search the decompiled 26.1.2 jar for a field that looks like a Map<BlockState,
// Holder<PoiType>> / Map<BlockState, RegistryEntry<PoiType>>, then fix the field name,
// its declaring class, and its type below to match.
// Confidence: Speculative / Risk: High - do not build against this without verifying first.
@Mixin(PoiTypes.class)
public interface PointOfInterestTypesAccessor {
  @Accessor("TYPE_BY_STATE") // GUESS - field name not confirmed
  static Map<BlockState, Holder<PoiType>> getPointOfInterestStatesToType() {
    throw new UnsupportedOperationException();
  }
}
