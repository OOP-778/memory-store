package com.oop.memorystore.expiring;

import java.util.concurrent.TimeUnit;

public class ExpiringPolicy {

  private final TimeUnit unit;
  private final long time;
  private final boolean shouldResetAfterAccess;

  protected ExpiringPolicy(TimeUnit unit, long time, boolean shouldResetAfterAccess) {
    this.unit = unit;
    this.time = time;
    this.shouldResetAfterAccess = shouldResetAfterAccess;
  }

  public static ExpiringPolicy create(long time, TimeUnit unit, boolean shouldResetAfterAccess) {
    return new ExpiringPolicy(unit, time, shouldResetAfterAccess);
  }

  public TimeUnit getUnit() {
    return unit;
  }

  public long getTime() {
    return time;
  }

  public boolean shouldResetAfterAccess() {
    return shouldResetAfterAccess;
  }
}
