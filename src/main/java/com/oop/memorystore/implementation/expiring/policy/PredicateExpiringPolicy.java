package com.oop.memorystore.implementation.expiring.policy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateExpiringPolicy<V>
    implements ExpiringPolicy<V, PredicateExpiringPolicy.EmptyExpirationData> {

  private final List<Predicate<V>> expirers = new LinkedList<>();

  public static <V> PredicateExpiringPolicy<V> create(final Predicate<V>... filters) {
    final PredicateExpiringPolicy<V> objectPredicateExpiringPolicy = new PredicateExpiringPolicy<>();
    objectPredicateExpiringPolicy.expirers.addAll(Arrays.asList(filters));
    return objectPredicateExpiringPolicy;
  }

  @Override
  public String named() {
    return "predicate";
  }

  @Override
  public EmptyExpirationData createExpirationData(final V value) {
    return null;
  }

  @Override
  public boolean checkExpiration(final V value, final EmptyExpirationData data) {
    for (final Predicate<V> expirer : this.expirers) {
        if (expirer.test(value)) {
            return true;
        }
    }

    return false;
  }

  @Override
  public void onAccess(final V value, final EmptyExpirationData data) {}

  public static class EmptyExpirationData implements ExpiringPolicy.ExpirationData {}
}
