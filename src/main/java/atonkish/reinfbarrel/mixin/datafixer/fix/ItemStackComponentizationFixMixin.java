package atonkish.reinfbarrel.mixin.datafixer.fix;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.serialization.Dynamic;

import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import atonkish.reinfbarrel.ReinforcedBarrelsMod;
import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfcore.util.ReinforcingMaterials;

// CONFIRMED against the decompiled reinforced-chests 26.1.2 jar, which has the exact same
// mixin for chests: package net.minecraft.datafixer.fix -> net.minecraft.util.datafix.fixes,
// inner class StackData -> ItemStackData, method fixBlockEntityData -> fixBlockEntityTag,
// data.itemMatches(...) -> data.is(...). Evidence: High confidence / Risk: Low.
@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
  @Inject(at = @At("RETURN"), method = "fixBlockEntityTag", cancellable = true)
  private static <T> void fixBlockEntityTag(
      ItemStackComponentizationFix.ItemStackData data,
      Dynamic<T> dynamic,
      String blockEntityId,
      CallbackInfoReturnable<Dynamic<T>> cir) {
    Set<String> itemIds = new HashSet<>();
    for (ReinforcingMaterial material : ReinforcingMaterials.MAP.values()) {
      itemIds.add(String.format("%s:%s_barrel", ReinforcedBarrelsMod.MOD_ID, material.getName()));
    }

    if (data.is(itemIds)) {
      List<Dynamic<T>> list =
          dynamic
              .get("Items")
              .asList(
                  itemsDynamic ->
                      itemsDynamic
                          .emptyMap()
                          .set(
                              "slot",
                              itemsDynamic.createInt(
                                  itemsDynamic.get("Slot").asByte((byte) 0) & 255))
                          .set("item", itemsDynamic.remove("Slot")));
      if (!list.isEmpty()) {
        data.setComponent("minecraft:container", dynamic.createList(list.stream()));
      }
      cir.setReturnValue(dynamic.remove("Items"));
    }
  }
}
