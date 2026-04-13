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

import de.leycm.i18label4j.CommonLabelProvider;
import de.leycm.i18label4j.LabelProvider;
import de.leycm.i18label4j.file.FileParser;
import de.leycm.i18label4j.mapping.MappingRule;
import de.leycm.i18label4j.serializer.KyoriAdventureSerializer;
import de.leycm.i18label4j.source.DirSource;
import de.leycm.init4j.instance.Instanceable;

import net.kyori.adventure.text.Component;
import picocli.CommandLine;

import java.net.URI;
import java.util.Locale;

// todo: add flag for --sun-misc-unsafe-memory-access=allow
@CommandLine.Command(name = "rubrion fallback")
public class FallbackPodCommand implements Runnable{

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"}) // cause: this is a command arg
    @CommandLine.Option(names = {"-d", "--domain"}, description = "the domain name of the server, e.g. rubrion.net")
    private String domain = "host";

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"}) // cause: this is a command arg
    @CommandLine.Option(names = {"-p", "--port"}, description = "the port to open an Minecraft server socket on")
    private int port = 25565;

    private FallbackInstance instance;
    private LabelProvider labelProvider;

    @Override
    public void run() {
        final URI uri = URI.create("https://rubrionmc.github.io/translation/");
        final DirSource source = new DirSource(uri, new FileParser.Json());

        labelProvider = CommonLabelProvider.builder()
                .defaultMappingRule(MappingRule.MINI_MESSAGE)
                .withSerializer(Component.class, new KyoriAdventureSerializer.KyoriMiniMessage())
                .buildWarm(source, Locale.US);

        Instanceable.register(labelProvider, LabelProvider.class);

        instance = new FallbackInstance(domain, port);
        Instanceable.register(instance, FallbackInstance.class);
    }

    public void stop() {
        Instanceable.unregister(instance.getClass());
        Instanceable.unregister(labelProvider.getClass());
    }

    static void main(final String[] args) {
        final FallbackPodCommand instance = new FallbackPodCommand();
        Runtime.getRuntime().addShutdownHook(new Thread(instance::stop, "shutdown"));
        new CommandLine(instance).execute(args);
    }
}
