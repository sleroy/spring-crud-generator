/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class SequoiaMeasureMain {
    public static void main(final String[] args) {
        final File             mDirectory   = new File("/home/sleroy/git/customers/sequoia_backend/sequoia_migration/181065-nexter-sequoia/java/sequoia-service-commun/src/main/java/com/osiatis/sequoia/service/impl");

        final Collection<File> controllers = FileUtils.listFiles(new File("/home/sleroy/git/customers/sequoia_backend/sequoia_migration/181065-nexter-sequoia/java/sequoia-classique/service/src/main/java/com/osiatis/sequoia/controller"), new String[]{"java"}, false);

        final Collection<File> managers    = FileUtils.listFiles(mDirectory, new String[]{"java"}, false);

        final File             rmDirectory      = new File("/home/sleroy/git/customers/sequoia_backend/sequoia_migration/181065-nexter-sequoia/java/sequoia-classique/service/src/main/java/com/osiatis/sequoia/service/impl/remote");
        final Collection<File> remoteManagers = FileUtils.listFiles(rmDirectory, new String[]{"java"}, false);


        final Set<String> rmManagerNames = remoteManagers.stream()
                                                         .map(File::getName)
                                                         .map(FilenameUtils::getBaseName)
                                                         .map(s -> StringUtils.remove(s, "Remote"))
                                                         .collect(Collectors.toSet());

        final Set<String> managerNames = managers.stream()
                                                 .map(File::getName)
                                                 .map(FilenameUtils::getBaseName)
                                                 .map(s -> StringUtils.remove(s, "Impl"))
                                                 .collect(Collectors.toSet());

        final Set<String> controllerNames = controllers.stream()
                                                       .map(f -> getManagerNameOfController(f))
                                                       .collect(Collectors.toSet());
        log.info("Remote managers {}", rmManagerNames.size());
        log.info("Managers {}", rmManagerNames.size());
        System.out.println(rmManagerNames);
        System.out.println(controllerNames);

        final Sets.SetView<String> found = Sets.difference(rmManagerNames, controllerNames);
        log.info("Missing Managers as REST API {}", found.size());
        System.out.println(found.stream().collect(Collectors.joining("\n")));

        // Count bindings and differences

        controllers.forEach(c -> {
                    final int cm = countControllerMappings(c);
                    final String      managerNameOfController = getManagerNameOfController(c);
                    final int pm =  countPublicMethods(new File(mDirectory, managerNameOfController + ".java"));
                    if ( cm == -1 || pm == -1) {
                        //System.out.println("Could not compute data for "+cm);
                    }
                    System.out.println(MessageFormat.format("{0},{1},{2},{3}", managerNameOfController, cm, pm, cm==pm));
                }

        );

    }

    private static int countPublicMethods(final File file) {
        try (final BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            final String str = IOUtils.toString(bufferedReader);
            final Pattern compile = Pattern.compile("public.*\\(.*\\{.*");
            final List<String> collect = Arrays.stream(str.split("\n"))
                                               .filter(s -> compile.matcher(s).find())
                                               .filter(s -> !s.contains(FilenameUtils.getBaseName(file.getName())))
                                               .collect(Collectors.toList());
            return collect.size();
        } catch (final IOException e) {
            //e.printStackTrace();
        }
        return -1;
    }

    @NotNull
    private static String getManagerNameOfController(final File f) {
        return StringUtils.remove(FilenameUtils.getBaseName(f.getName()), "Controller") + "ManagerImpl";
    }

    private static int countControllerMappings(final File c) {
        try (final BufferedReader bufferedReader = Files.newBufferedReader(c.toPath(), StandardCharsets.UTF_8)) {
            final String str = IOUtils.toString(bufferedReader);
            return StringUtils.countMatches(str, "@RequestMapping(method");
        } catch (final IOException e) {
            //e.printStackTrace();
        }
        return -1;
    }
}

