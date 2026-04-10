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

import de.leycm.init4j.instance.Instanceable;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.client.handshake.ClientHandshakePacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;
import net.minestom.server.ping.Status;

import lombok.NonNull;
import lombok.Getter;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

@Getter
public class FallbackInstance implements Instanceable {


    public static @NonNull FallbackInstance getInstance() {
        return Instanceable.getInstance(FallbackInstance.class);
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

        System.out.println("Starting fallback server on port " + startPort + " with domain suffix " + domainSuffix);

        server.start("0.0.0.0", startPort);
    }

    @Override
    public void onUninstall() {
        MinecraftServer.stopCleanly();
    }

    @SuppressWarnings("UnstableApiUsage") // cause: the protocol version is getting dropped by listening on the ClientHandshakePacket
    private void handleHandshakePacket(
            final @NonNull ClientHandshakePacket packet,
            final @NonNull PlayerConnection connection
    ) {
        if (!(connection instanceof PlayerSocketConnection socketConnection)) return;

        // note: hide from server scener by only accepting domain connections
        if (!packet.serverAddress().endsWith(domainSuffix)) {
            System.out.println("Rejecting connection from " + packet.serverAddress() + " because it does not end with " + domainSuffix);
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
        // note: catching ordinal 1
        event.setStatus(Status.builder()
                .description(Component.text("A Fallback Server"))
                .build());
    }
}
