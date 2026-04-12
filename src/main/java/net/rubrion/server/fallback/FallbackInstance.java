/*
 * This file is part of the Rubrion Group.
 *
 * Licensed under the Rubrion Public License (RPL), Version 1, 2026.
 * You may not use this file except in compliance with the License.
 *
 * License:
 * https://rubrionmc.github.io/.github/licensens/RUBRION_PUBLIC_LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * Copyright (c) 2024 Rubrion Group. All rights reserved.
 */
package net.rubrion.server.fallback;

import de.leycm.i18label4j.Label;
import de.leycm.init4j.instance.Instanceable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.client.handshake.ClientHandshakePacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;
import net.minestom.server.ping.Status;

import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;
import lombok.Getter;
import net.rubrion.utils.protocol.ProtocolResolver;
import net.rubrion.utils.version.Version;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

@Getter @Slf4j
public class FallbackInstance implements Instanceable {

    private static final Status.PlayerInfo PLAYER_INFO = Status.PlayerInfo.builder()
            .onlinePlayers(0)
            .maxPlayers(0)
            .sample("§fNo Connection Found!")
            .sample("§7Fail to connect you to")
            .sample("§7a healthy §fRubrion §7server.")
            .build();


    private static final Status.VersionInfo VERSION_INFO = new Status.VersionInfo(
            "§70§8/§40§r",
            -1);

    private static final ProtocolResolver<String> N_A_STRING = ProtocolResolver.with("N/A")
            .since(Version.V1_16_0, "ɴ/ᴀ");

    public static @NonNull FallbackInstance getInstance() {
        return Instanceable.getInstance(FallbackInstance.class);
    }

    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        for (int i = 1; i < sb.length(); i++) {
            sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
        }
        return sb.toString();
    }

    private final @NonNull VarHandle protocolHandle;
    private final @NonNull String domainSuffix;
    private final int startPort;

    @SuppressWarnings("UnstableApiUsage") // cause: we need this to set the protocol version for the connection
    public FallbackInstance(final @NonNull String domainSuffix, final int startPort) {
        this.domainSuffix = domainSuffix;
        this.startPort = startPort;

        try {
            final Field field = PlayerSocketConnection.class.getDeclaredField("protocolVersion");
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(PlayerSocketConnection.class, MethodHandles.lookup());
            protocolHandle = lookup.unreflectVarHandle(field);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError("Failed to init protocolVersion field");
        }
    }

    private MinecraftServer server;
    private PacketListenerManager packetHandler;
    private GlobalEventHandler eventHandler;

    @Override
    public void onInstall() {
        this.server = MinecraftServer.init();
        this.packetHandler = MinecraftServer.getPacketListenerManager();
        this.eventHandler = MinecraftServer.getGlobalEventHandler();

        packetHandler.setListener(ConnectionState.HANDSHAKE, ClientHandshakePacket.class, this::handleHandshakePacket);
        eventHandler.addListener(ServerListPingEvent.class, this::handleServerListPing);

        log.info("Starting fallback server on port {} with domain suffix {}", startPort, domainSuffix);
        server.start("0.0.0.0", startPort);
    }

    @Override
    public void onUninstall() {
        log.info("Shoutdown fallback server on port {} with domain suffix {}", startPort, domainSuffix);
        MinecraftServer.stopCleanly();
    }

    @SuppressWarnings("UnstableApiUsage") // cause: the protocol version is getting dropped by listening on the ClientHandshakePacket
    private void handleHandshakePacket(
            final @NonNull ClientHandshakePacket packet,
            final @NonNull PlayerConnection connection
    ) {
        if (!(connection instanceof PlayerSocketConnection socketConnection)) return;

        // note: hide from server scener by only accepting domain connections
        if (!packet.serverAddress().toLowerCase().endsWith(domainSuffix.toLowerCase())) {
            try { socketConnection.getChannel().close(); }
            catch (IOException _) { }
            return;
        }

        // setting connection packet version to the players one for server side control
        protocolHandle.set(socketConnection, packet.protocolVersion());

        // note: 0 it a server ping handshake, witch is managed below
        if (packet.intent().ordinal() == 0) return;

        // kick player
        connection.kick(Component.text("This server is a fallback server, and " +
                "does not accept connections. Please connect to the main server instead."));
        connection.disconnect();
    }

    private void handleServerListPing(final @NonNull ServerListPingEvent event) {
        if (event.getConnection() == null) return;
        final int protocolVersion = event.getConnection().getProtocolVersion();

        final Component description = Component.empty()
                .append(Component.text(capitalizeFirstLetter(domainSuffix), TextColor.color(0xff0000)))
                .append(Component.space())
                .append(Component.text(N_A_STRING.resolve(protocolVersion), NamedTextColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.text(Label.of("tip.rubrion.motd.helloworld").rawOfDefault(), NamedTextColor.GRAY));
                // todo: add more and moment depending tips

        // note: catching ordinal 1
        event.setStatus(Status.builder()
                .playerInfo(PLAYER_INFO)
                .versionInfo(VERSION_INFO)
                .description(description)
                .build());
    }
}
