package atonkish.reinfbarrel.mixin.datafixer.schema;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1460;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import atonkish.reinfbarrel.ReinforcedBarrelsMod;

// CONFIRMED against the decompiled reinforced-chests 26.1.2 jar, which has the exact same
// mixin for chests: class net.minecraft.datafixer.schema.Schema1460 -> net.minecraft.util
// .datafix.schemas.V1460, and net.minecraft.datafixer.TypeReferences -> net.minecraft.util
// .datafix.fixes.References. Evidence: High confidence / Risk: Low.
@Mixin(V1460.class)
public class Schema1460Mixin {
  @Inject(at = @At("RETURN"), method = "registerBlockEntities", cancellable = true)
  private void registerBlockEntities(
      Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
    Map<String, Supplier<TypeTemplate>> map = cir.getReturnValue();

    // TODO: materials should be able to be resolved dynamically.
    for (String material : List.of("copper", "iron", "gold", "diamond", "netherite")) {
      schema.register(
          map,
          String.format("%s:%s_barrel", ReinforcedBarrelsMod.MOD_ID, material),
          () -> {
            return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema)));
          });
    }
  }
}
