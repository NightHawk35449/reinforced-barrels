package atonkish.reinfbarrel.gametest.util;

import java.util.Locale;

import net.minecraft.resources.Identifier;

// CONFIRMED against decompiled reinforced-chests 26.1.2 TestIdentifier.java: Identifier.of(...)
// -> Identifier.fromNamespaceAndPath(...), package net.minecraft.util -> net.minecraft.resources.
public class TestIdentifier {
  public static Identifier of(String namespace, Class<?> testClass, String name) {
    return Identifier.fromNamespaceAndPath(
        namespace,
        camelToSnake(String.format("%s/%s", testClass.getSimpleName(), name).replace(" ", "_")));
  }

  private static String camelToSnake(String input) {
    return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
  }
}
