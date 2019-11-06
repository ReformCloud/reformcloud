package systems.reformcloud.reformcloud2.executor.api.common.patch;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface Patcher {

    /**
     * @return All loaded patches which are not applied to the runtime
     */
    @Nonnull
    List<Patch> patches();

    /**
     * @return If new patches are available
     */
    boolean hasPatches();

    /**
     * Pauses the patches to the given time
     *
     * @param time The time how long the patches should not get loaded
     * @param timeUnit The unit of the length of the time
     */
    void pausePatches(long time, TimeUnit timeUnit);

    /**
     * Resumes the patches if they are paused
     */
    void resumePatches();

    /**
     * Loads all patches which got published after the last check
     *
     * @param lastCheck The last time a check for patches was made
     */
    void loadPatches(long lastCheck);

    /**
     * Applies the patches to the runtime
     */
    void doPatches();
}
