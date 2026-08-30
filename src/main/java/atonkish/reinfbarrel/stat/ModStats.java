package atonkish.reinfbarrel.stat;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

import atonkish.reinfcore.util.ReinforcingMaterial;

// CONFIRMED against decompiled reinforced-chests 26.1.2 ModStats.java (identical pattern):
// package net.minecraft.stat -> net.minecraft.stats, Registries.CUSTOM_STAT (for the register
// call) -> BuiltInRegistries.CUSTOM_STAT, Stats.CUSTOM.getOrCreateStat(...) ->
// Stats.CUSTOM.get(...).
public class ModStats {
  public static final Map<ReinforcingMaterial, Identifier> OPEN_REINFORCED_BARREL_MAP =
      new LinkedHashMap<>();

  public static Identifier registerMaterialOpen(String namespace, ReinforcingMaterial material) {
    if (!OPEN_REINFORCED_BARREL_MAP.containsKey(material)) {
      String id = "open_" + material.getName() + "_barrel";
      Identifier identifier = ModStats.register(namespace, id, StatFormatter.DEFAULT);
      OPEN_REINFORCED_BARREL_MAP.put(material, identifier);
    }

    return OPEN_REINFORCED_BARREL_MAP.get(material);
  }

  private static Identifier register(String namespace, String id, StatFormatter formatter) {
    Identifier identifier = Identifier.fromNamespaceAndPath(namespace, id);
    Registry.register(BuiltInRegistries.CUSTOM_STAT, id, identifier);
    Stats.CUSTOM.get(identifier, formatter);
    return identifier;
  }
}
