package de.klaro.reformcloud2.executor.api.common.patch;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface Patcher {

    List<Patch> patches();

    boolean hasPatches();

    void pausePatches(long time, TimeUnit timeUnit);

    void resumePatches();

    void loadPatches(long lastCheck);

    void doPatches();
}
