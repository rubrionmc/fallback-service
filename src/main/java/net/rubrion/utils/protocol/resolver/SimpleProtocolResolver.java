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

import lombok.NonNull;
import net.rubrion.utils.version.Version;

public class SimpleProtocolResolver<T> extends ProtocolResolverMap<SimpleProtocolResolver<T>, T> {

    public static @NonNull <T> SimpleProtocolResolver<T> with(final @NonNull T value) {
        SimpleProtocolResolver<T> resolver = new SimpleProtocolResolver<>();
        return resolver.since(Version.EXISTENCE, value);
    }

    private SimpleProtocolResolver() { }

}
