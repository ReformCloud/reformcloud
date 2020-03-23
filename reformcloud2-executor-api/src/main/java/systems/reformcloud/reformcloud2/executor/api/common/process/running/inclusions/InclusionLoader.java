package systems.reformcloud.reformcloud2.executor.api.common.process.running.inclusions;

import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;

public final class InclusionLoader {

    private InclusionLoader() {
        throw new UnsupportedOperationException();
    }

    public static void loadInclusions(@Nonnull Path processPath, @Nonnull Collection<ProcessInclusion> inclusions) {
        inclusions.forEach(inclusion -> {
            Path target = Paths.get("reformcloud/files/inclusions", inclusion.getName());
            if (Files.exists(target)) {
                return;
            }

            DownloadHelper.openConnection(
                    inclusion.getUrl(),
                    new HashMap<>(),
                    stream -> {
                        try {
                            SystemHelper.createDirectory(target.getParent());
                            Files.copy(stream, target);
                        } catch (final Throwable throwable) {
                            System.err.println("Unable to prepare inclusion " + inclusion.getName() + "!");
                            System.err.println("The error was: " + throwable.getMessage());
                        }
                    }, throwable -> {
                        System.err.println("Unable to open connection to given download server " + inclusion.getUrl());
                        System.err.println("The error was: " + throwable.getMessage());
                    }
            );
        });

        SystemHelper.copyDirectory(Paths.get("reformcloud/files/inclusions"), processPath);
    }
}
