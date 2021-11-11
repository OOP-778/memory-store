package com.oop.memorystore.implementation.expiring.policy;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TimedExpiringPolicy<V>
    implements ExpiringPolicy<V, TimedExpiringPolicy.TimedExpirationData> {

  private final Function<V, TimedExpirationData> expirationDataFunction;

  protected TimedExpiringPolicy(final Function<V, TimedExpirationData> expirationDataFunction) {
    this.expirationDataFunction = expirationDataFunction;
  }

  public static <V> ExpiringPolicy<V, TimedExpirationData> create(
      final long time, final TimeUnit unit, final boolean shouldResetAfterAccess) {
    return new TimedExpiringPolicy<>(
        $ -> new TimedExpirationData(unit, time, shouldResetAfterAccess));
  }

  public static <V> ExpiringPolicy<V, TimedExpirationData> create(
      final Function<V, TimedExpirationData> expirationDataFunction) {
    return new TimedExpiringPolicy<>(expirationDataFunction);
  }

  @Override
  public String named() {
    return "timed";
  }

  @Override
  public TimedExpirationData createExpirationData(final V value) {
    return this.expirationDataFunction.apply(value);
  }

  @Override
  public boolean checkExpiration(final V value, final TimedExpirationData data) {
    return (System.currentTimeMillis() - data.lastFetched) >= data.unit.toMillis(data.time);
  }

  @Override
  public void onAccess(final V value, final TimedExpirationData data) {
      if (!data.shouldResetAfterAccess) {
          return;
      }

      data.lastFetched = System.currentTimeMillis();
  }

  public static class TimedExpirationData implements ExpiringPolicy.ExpirationData {
    private final TimeUnit unit;
    private final long time;
    private final boolean shouldResetAfterAccess;
    /** When was reference last fetched. By default it's set to it's creation time */
    private long lastFetched = System.currentTimeMillis();

    public TimedExpirationData(final TimeUnit unit, final long time, final boolean shouldResetAfterAccess) {
      this.unit = unit;
      this.time = time;
      this.shouldResetAfterAccess = shouldResetAfterAccess;
    }
  }
}
