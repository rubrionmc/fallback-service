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

import net.rubrion.utils.protocol.ProtocolResolver;
import net.rubrion.utils.version.Version;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ProtocolResolverMap<I extends ProtocolResolverMap<I, T>, T> implements ProtocolResolver<T> {

    private final NavigableMap<Integer, T> versionMap = new TreeMap<>();

    protected ProtocolResolverMap() { }

    public @NonNull I since(final @NonNull Version version,
                                                 final @NonNull T value) {
        return since(version.getProtocol(), value);
    }

    @SuppressWarnings("unchecked") // cause: we force this
    public @NonNull I since(final int protocol,
                            final @NonNull T value) {
        if (versionMap.containsKey(protocol)) {
            throw new IllegalStateException("A Value for protocol " + protocol + " is already registered");
        }

        versionMap.put(protocol, value);
        return (I) this;
    }

    public @Nullable T resolveOrNull(final int protocol) {
        Map.Entry<Integer, T> entry = versionMap.floorEntry(protocol);
        return entry.getValue();
    }

    @Override
    public int lowestSupportedProtocols() {
        return versionMap.firstKey();
    }

}
