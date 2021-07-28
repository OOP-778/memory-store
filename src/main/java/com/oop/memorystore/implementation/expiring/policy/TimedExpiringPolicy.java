package com.oop.memorystore.implementation.expiring.policy;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TimedExpiringPolicy<V>
    implements ExpiringPolicy<V, TimedExpiringPolicy.TimedExpirationData> {

  private final Function<V, TimedExpirationData> expirationDataFunction;

  protected TimedExpiringPolicy(Function<V, TimedExpirationData> expirationDataFunction) {
    this.expirationDataFunction = expirationDataFunction;
  }

  public static <V> ExpiringPolicy<V, TimedExpirationData> create(
      long time, TimeUnit unit, boolean shouldResetAfterAccess) {
    return new TimedExpiringPolicy<>(
        $ -> new TimedExpirationData(unit, time, shouldResetAfterAccess));
  }

  public static <V> ExpiringPolicy<V, TimedExpirationData> create(
      Function<V, TimedExpirationData> expirationDataFunction) {
    return new TimedExpiringPolicy<>(expirationDataFunction);
  }

  @Override
  public String named() {
    return "timed";
  }

  @Override
  public TimedExpirationData createExpirationData(V value) {
    return expirationDataFunction.apply(value);
  }

  @Override
  public boolean checkExpiration(V value, TimedExpirationData data) {
    return (System.currentTimeMillis() - data.lastFetched) >= data.unit.toMillis(data.time);
  }

  @Override
  public void onAccess(V value, TimedExpirationData data) {
      if (!data.shouldResetAfterAccess) {
          return;
      }

      data.lastFetched = System.currentTimeMillis();
  }

  public static class TimedExpirationData implements ExpirationData {
    private final TimeUnit unit;
    private final long time;
    private final boolean shouldResetAfterAccess;
    /** When was reference last fetched. By default it's set to it's creation time */
    private long lastFetched = System.currentTimeMillis();

    public TimedExpirationData(TimeUnit unit, long time, boolean shouldResetAfterAccess) {
      this.unit = unit;
      this.time = time;
      this.shouldResetAfterAccess = shouldResetAfterAccess;
    }
  }
}
