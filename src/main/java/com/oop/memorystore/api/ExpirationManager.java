package com.oop.memorystore.api;

import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy.ExpirationData;
import com.sun.istack.internal.NotNull;

public interface ExpirationManager<V> {

    void onAdd(final V value);

    void onRemove(final V value);

    boolean checkExpiration(V value);

    ExpirationData getExpirationData(final V value, @NotNull final Class<? extends ExpiringPolicy<?, ?>> policyClass);
}
