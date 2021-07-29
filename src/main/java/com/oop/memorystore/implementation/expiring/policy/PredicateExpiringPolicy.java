package com.oop.memorystore.implementation.expiring.policy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateExpiringPolicy<V>
    implements ExpiringPolicy<V, PredicateExpiringPolicy.EmptyExpirationData> {

  private final List<Predicate<V>> expirers = new LinkedList<>();

  public static <V> PredicateExpiringPolicy<V> create(Predicate<V>... filters) {
    PredicateExpiringPolicy<V> objectPredicateExpiringPolicy = new PredicateExpiringPolicy<>();
    objectPredicateExpiringPolicy.expirers.addAll(Arrays.asList(filters));
    return objectPredicateExpiringPolicy;
  }

  @Override
  public String named() {
    return "predicate";
  }

  @Override
  public EmptyExpirationData createExpirationData(V value) {
    return null;
  }

  @Override
  public boolean checkExpiration(V value, EmptyExpirationData data) {
    for (Predicate<V> expirer : expirers) {
        if (expirer.test(value)) {
            return true;
        }
    }

    return false;
  }

  @Override
  public void onAccess(V value, EmptyExpirationData data) {}

  public static class EmptyExpirationData implements ExpiringPolicy.ExpirationData {}
}
