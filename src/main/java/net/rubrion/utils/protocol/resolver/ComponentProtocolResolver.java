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
package net.rubrion.utils.protocol.resolver;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.rubrion.utils.protocol.ProtocolResolver;
import net.rubrion.utils.version.Version;

import net.kyori.adventure.text.Component;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ComponentProtocolResolver implements ProtocolResolver<Component> {

    private static final Version SUPPORTED_VERSION = Version.V1_7_0;
    private static final Version TRUE_TYPE_VERSION = Version.V1_7_0;

    public static @NonNull Component downgradeColors(final @NonNull Component component) {
        final TextColor color = component.color();
        final Component result;

        if (color != null && !(color instanceof NamedTextColor)) {
            result = component.color(NamedTextColor.nearestTo(color));
        } else { result = component; }

        final List<Component> children = component.children().stream()
                .map(ComponentProtocolResolver::downgradeColors)
                .collect(Collectors.toList());

        return result.children(children);
    }


    public static @NonNull ComponentProtocolResolver with(final @NonNull Component original) {
        return new ComponentProtocolResolver(original);
    }

    private final @NonNull Component original;
    private @Nullable Component downgraded;

    private ComponentProtocolResolver(final @NonNull Component original) {
        this.original = original;
    }

    @Override
    public @Nullable Component resolveOrNull(final int protocol) {

        if (protocol >= TRUE_TYPE_VERSION.getProtocol()) {
            return original;
        }

        if (protocol >= SUPPORTED_VERSION.getProtocol()) {
            if (downgraded == null) {
                downgraded = downgradeColors(original);
            }

            return downgraded;
        }

        // note: return null for unsupported versions
        return null;
    }

    @Override
    public int lowestSupportedProtocols() {
        return SUPPORTED_VERSION.getProtocol();
    }

}
