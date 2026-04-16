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
package net.rubrion.utils.version;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.Comparator;

@Getter
public enum Version {

    // max used for use cases like never
    VMAX_MAX_MAX(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),

    // 26.x
    V26_1_1(26, 1, 0, 775),
    V26_1_0(26, 1, 0, 775),

    // 1.21.x
    V1_21_11(1, 21, 11, 774),
    V1_21_10(1, 21, 10, 773),
    V1_21_9 (1, 21, 9,  773),
    V1_21_8 (1, 21, 8,  772),
    V1_21_7 (1, 21, 7,  772),
    V1_21_6 (1, 21, 6,  771),
    V1_21_5 (1, 21, 5,  770),
    V1_21_4 (1, 21, 4,  769),
    V1_21_3 (1, 21, 3,  768),
    V1_21_2 (1, 21, 2,  768),
    V1_21_1 (1, 21, 1,  767),
    V1_21_0 (1, 21, 0,  767),

    // 1.20.x
    V1_20_6(1, 20, 6, 766),
    V1_20_5(1, 20, 5, 766),
    V1_20_4(1, 20, 4, 765),
    V1_20_3(1, 20, 3, 764),
    V1_20_2(1, 20, 2, 764),
    V1_20_1(1, 20, 1, 763),
    V1_20_0(1, 20, 0, 763),

    // 1.19.x
    V1_19_4(1, 19, 4, 762),
    V1_19_3(1, 19, 3, 761),
    V1_19_2(1, 19, 2, 760),
    V1_19_1(1, 19, 1, 760),
    V1_19_0(1, 19, 0, 759),

    // 1.18.x
    V1_18_2(1, 18, 2, 758),
    V1_18_1(1, 18, 1, 757),
    V1_18_0(1, 18, 0, 757),

    // 1.17.x
    V1_17_1(1, 17, 1, 756),
    V1_17_0(1, 17, 0, 755),

    // 1.16.x
    V1_16_5(1, 16, 5, 754),
    V1_16_4(1, 16, 4, 754),
    V1_16_3(1, 16, 3, 753),
    V1_16_2(1, 16, 2, 751),
    V1_16_1(1, 16, 1, 736),
    V1_16_0(1, 16, 0, 735),

    // 1.15.x
    V1_15_2(1, 15, 2, 578),
    V1_15_1(1, 15, 1, 575),
    V1_15_0(1, 15, 0, 573),

    // 1.14.x
    V1_14_4(1, 14, 4, 498),
    V1_14_3(1, 14, 3, 490),
    V1_14_2(1, 14, 2, 485),
    V1_14_1(1, 14, 1, 480),
    V1_14_0(1, 14, 0, 477),

    // 1.13.x
    V1_13_2(1, 13, 2, 404),
    V1_13_1(1, 13, 1, 401),
    V1_13_0(1, 13, 0, 393),

    // 1.12.x
    V1_12_2(1, 12, 2, 340),
    V1_12_1(1, 12, 1, 338),
    V1_12_0(1, 12, 0, 335),

    // 1.11.x
    V1_11_2(1, 11, 2, 316),
    V1_11_1(1, 11, 1, 316),
    V1_11_0(1, 11, 0, 315),

    // 1.10.x
    V1_10_2(1, 10, 2, 210),
    V1_10_1(1, 10, 1, 210),
    V1_10_0(1, 10, 0, 210),

    // 1.9.x
    V1_9_4(1, 9, 4, 110),
    V1_9_3(1, 9, 3, 110),
    V1_9_2(1, 9, 2, 110),
    V1_9_1(1, 9, 1, 108),
    V1_9_0(1, 9, 0, 107),

    // 1.8.x
    V1_8_9(1, 8, 9, 47),
    V1_8_8(1, 8, 8, 47),
    V1_8_7(1, 8, 7, 47),
    V1_8_6(1, 8, 6, 47),
    V1_8_5(1, 8, 5, 47),
    V1_8_4(1, 8, 4, 47),
    V1_8_3(1, 8, 3, 47),
    V1_8_2(1, 8, 2, 47),
    V1_8_1(1, 8, 1, 47),
    V1_8_0(1, 8, 0, 47),

    // 1.7.x
    V1_7_10(1, 7, 10, 5),
    V1_7_9 (1, 7, 9,  5),
    V1_7_8 (1, 7, 8,  5),
    V1_7_7 (1, 7, 7,  5),
    V1_7_6 (1, 7, 6,  5),
    V1_7_5 (1, 7, 5,  4),
    V1_7_4 (1, 7, 4,  4),
    V1_7_3 (1, 7, 3,  4),
    V1_7_2 (1, 7, 2,  4),
    V1_7_1 (1, 7, 1,  3),
    V1_7_0 (1, 7, 0,  3),

    // 0.0.0 (for unknown versions, we can use this as a fallback)
    V0_0_0(0, 0, 0, 0);

    // note: these can change over time
    public static final Version NEVER = VMAX_MAX_MAX;
    public static final Version LATEST = V26_1_1;
    public static final Version EXISTENCE = V0_0_0;

    private final int major;
    private final int minor;
    private final int patch;
    private final int protocol;

    Version(int major, int minor, int patch, int protocol) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public int toSemInt() {
        return major * 1_000_000 + minor * 1_000 + patch;
    }

    public int toProtocol() {
        return getProtocol();
    }

    public boolean isNewerThan(final @NonNull Version other) {
        return this.toSemInt() > other.toSemInt();
    }

    public boolean isOlderThan(final @NonNull Version other) {
        return this.toSemInt() < other.toSemInt();
    }

    public boolean isNewerThan(final int protocol) {
        return this.protocol > protocol;
    }

    public boolean isOlderThan(final int protocol) {
        return this.protocol < protocol;
    }

    public static @NonNull Version fromString(final @NonNull String s) {
        final String[] parts = s.split("\\.");
        if (parts.length < 2 || parts.length > 3)
            throw new IllegalArgumentException("Invalid version: " + s);

        final int maj = Integer.parseInt(parts[0]);
        final int min = Integer.parseInt(parts[1]);
        final int pat = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;

        return Arrays.stream(values())
                .filter(v -> v.major == maj && v.minor == min && v.patch == pat)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown version: " + s));
    }

    public static @NonNull Version fromProtocol(final int protocol) {
        return Arrays.stream(values())
                .filter(v -> v.protocol == protocol)
                .max(Comparator.comparingInt(Version::toSemInt))
                .orElseThrow(() -> new IllegalArgumentException("Unknown protocol: " + protocol));
    }

    public static @NonNull Version resolveBestMatch(int protocol) {
        return Arrays.stream(values())
                .filter(v -> v.protocol <= protocol)
                .max(Comparator.comparingInt(Version::toSemInt))
                .orElse(EXISTENCE);
    }

    @Contract(pure = true)
    public @NonNull String debug() {
        return "Version{" +
                "name=" + this.name() +
                ", version=" + this.toString() +
                ", int=" + this.toSemInt() +
                ", major=" + this.major +
                ", minor=" + this.minor +
                ", patch=" + this.patch +
                ", protocol=" + this.protocol +
                '}';
    }

}
