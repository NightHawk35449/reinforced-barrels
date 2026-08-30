package atonkish.reinfbarrel.gametest.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.mojang.authlib.GameProfile;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import io.netty.channel.embedded.EmbeddedChannel;

// CONFIRMED against decompiled reinforced-chests 26.1.2 MockServerPlayerHelper.java (identical
// pattern): TestContext -> GameTestHelper, ClientConnection -> Connection, NetworkSide ->
// PacketFlow, ConnectedClientData -> CommonListenerCookie (createDefault -> createInitial,
// syncedOptions() -> clientInformation()), ServerPlayerEntity -> ServerPlayer, GameMode ->
// GameType, Vec3d -> Vec3, Text -> Component (Text.of(...) -> Component.literal(...)),
// context.getWorld() -> context.getLevel(), context.getAbsolute(...) -> context.absoluteVec(...),
// player.changeGameMode -> player.setGameMode, player.setPosition -> player.setPos,
// player.getInventory().clear() -> clearContent(), player.networkHandler -> player.connection,
// getUuidAsString() -> getStringUUID().
public class MockServerPlayerHelper {
  private static AtomicInteger playerId = new AtomicInteger(1);

  public static ServerPlayer spawn(GameTestHelper context, GameType gameMode, Vec3 pos) {
    CommonListenerCookie cookie =
        CommonListenerCookie.createInitial(
            new GameProfile(
                UUID.randomUUID(), String.format("player-%d", playerId.getAndIncrement())),
            false);
    ServerPlayer player =
        new ServerPlayer(
            context.getLevel().getServer(),
            context.getLevel(),
            cookie.gameProfile(),
            cookie.clientInformation());
    Connection connection = new Connection(PacketFlow.SERVERBOUND);
    new EmbeddedChannel(connection);
    context.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);

    player.setGameMode(gameMode);
    player.setPos(context.absoluteVec(pos));
    player.setOnGround(true);

    return player;
  }

  public static void destroy(GameTestHelper context, ServerPlayer player) {
    player.discard();
    player.getInventory().clearContent();
    player.connection.disconnect(
        Component.literal(
            String.format(
                "%s (%s) left the game", player.getGameProfile().name(), player.getStringUUID())));
  }
}
