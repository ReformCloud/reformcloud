package systems.reformcloud.reformcloud2.executor.api.common.patch.basic;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.patch.Patch;

public final class DefaultPatch implements Patch {

  public DefaultPatch(long releaseDate, String downloadURL) {
    this.releaseDate = releaseDate;
    this.downloadURL = downloadURL;
  }

  private final long releaseDate;

  private final String downloadURL;

  public long getReleaseDate() { return releaseDate; }

  @Nonnull
  public String getDownloadURL() {
    return downloadURL;
  }
}
