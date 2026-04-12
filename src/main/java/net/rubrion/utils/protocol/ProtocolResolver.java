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
package net.rubrion.utils.protocol;

import net.rubrion.utils.exception.NotSupportedProtocol;
import net.rubrion.utils.protocol.resolver.ComponentProtocolResolver;
import net.rubrion.utils.protocol.resolver.SimpleProtocolResolver;

import net.kyori.adventure.text.Component;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ProtocolResolver<T> {

    static @NonNull <T> SimpleProtocolResolver<T> with(final @NonNull T value) {
        return SimpleProtocolResolver.with(value);
    }

    static @NonNull ComponentProtocolResolver component(final @NonNull Component value) {
        return ComponentProtocolResolver.with(value);
    }

    @Nullable T resolveOrNull(int protocol);

    int lowestSupportedProtocols();

    default @NonNull T resolve(final int protocol) {
        T value = resolveOrNull(protocol);
        if (value == null) throw new NotSupportedProtocol(protocol,
                lowestSupportedProtocols());

        return value;
    }

    default <E extends Throwable> @NonNull T resolveOrThrow(
            final int protocol,
            final @NonNull Supplier<E> supplier
    ) throws E {
        T value = resolveOrNull(protocol);
        if (value == null) throw supplier.get();

        return value;
    }

    default boolean has(final int protocol) {
        return resolveOrNull(protocol) != null;
    }
}