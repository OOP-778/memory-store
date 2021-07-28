package com.oop.memorystore.implementation.expiring.policy;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateExpiringPolicy<V> implements ExpiringPolicy<V, PredicateExpiringPolicy.EmptyExpirationData> {

    private final List<Predicate<V>> expirers = new LinkedList<>();

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
        return false;
    }

    @Override
    public void onAccess(V value, EmptyExpirationData data) {}

    public static class EmptyExpirationData implements ExpirationData {}

}
