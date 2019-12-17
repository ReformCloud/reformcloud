package systems.reformcloud.reformcloud2.permissions.util.unit;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class InternalTimeUnit {

  private InternalTimeUnit() { throw new UnsupportedOperationException(); }

  public static long convert(@Nullable TimeUnit timeUnit, long time) {
    return timeUnit != null ? timeUnit.toMillis(time) : convertMonth(time);
  }

  private static long convertMonth(long time) {
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.add(Calendar.MONTH, toInt(time));
    return calendar.getTimeInMillis();
  }

  private static int toInt(long l) {
    if (l > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }

    return l < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int)l;
  }
}
