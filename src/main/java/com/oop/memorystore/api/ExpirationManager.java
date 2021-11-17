package com.oop.memorystore.api;

import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy.ExpirationData;
import java.util.function.Consumer;

public interface ExpirationManager<V> {

    void onAdd(final V value);

    void onRemove(final V value);

    boolean checkExpiration(V value);

    <T extends ExpirationData, E extends ExpiringPolicy> T getExpirationData(final V value, final Class<E> policyClass);

    void addGlobalExpireListener(Consumer<V> listener);
}
