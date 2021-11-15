package com.oop.memorystore.implementation.expiring;

import com.oop.memorystore.api.ExpirationManager;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy.ExpirationData;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Class to handle expirations
 *
 * @param <V>
 */
public class DefaultExpirationManager<V> implements ExpirationManager<V> {
    private final Map<ExpiringPolicy<V, ?>, Map<V, ExpiringPolicy.ExpirationData>> policyData =
        new HashMap<>();

    private final List<Consumer<V>> globalExpireListeners = new LinkedList<>();

    @SafeVarargs
    public DefaultExpirationManager(final ExpiringPolicy<V, ?>... policies) {
        for (final ExpiringPolicy<V, ?> policy : policies) {
            this.policyData.put(policy, new HashMap<>());
        }
    }

    @Override
    public void addGlobalExpireListener(final Consumer<V> listener) {
        this.globalExpireListeners.add(listener);
    }

    public boolean checkExpiration(final V value) {
        boolean shouldExpire = false;
        for (final Map.Entry<
            ExpiringPolicy<V, ? extends ExpiringPolicy.ExpirationData>,
            Map<V, ExpiringPolicy.ExpirationData>>
            policyEntry : this.policyData.entrySet()) {
            final ExpiringPolicy policy = policyEntry.getKey();

            shouldExpire = policy.checkExpiration(value, policyEntry.getValue().get(value));
            if (shouldExpire) {
                break;
            }
        }

        return shouldExpire;
    }

    public <T extends ExpirationData, E extends ExpiringPolicy> T getExpirationData(final V value, final Class<E> policyClass) {
        for (final Entry<ExpiringPolicy<V, ?>, Map<V, ExpirationData>> policyEntry : this.policyData.entrySet()) {
            if (!policyClass.isAssignableFrom(policyEntry.getKey().getClass())) {
                continue;
            }

            return (T) policyEntry.getValue().get(value);
        }

        return null;
    }

    public void onAdd(final V value) {
        for (final Map.Entry<ExpiringPolicy<V, ?>, Map<V, ExpiringPolicy.ExpirationData>> policyEntry :
            this.policyData.entrySet()) {
            final ExpiringPolicy.ExpirationData expirationData =
                policyEntry.getKey().createExpirationData(value);
            if (expirationData == null) {
                continue;
            }

            policyEntry.getValue().put(value, expirationData);
        }
    }

    public void onRemove(final V value) {
        for (final Entry<ExpiringPolicy<V, ?>, Map<V, ExpirationData>> policyData : this.policyData.entrySet()) {
            policyData.getValue().remove(value);
        }
    }

    public void onAccess(final V value) {
        for (final Map.Entry<
            ExpiringPolicy<V, ? extends ExpiringPolicy.ExpirationData>,
            Map<V, ExpiringPolicy.ExpirationData>>
            policyEntry : this.policyData.entrySet()) {
            final ExpiringPolicy policy = policyEntry.getKey();

            policy.onAccess(value, policyEntry.getValue().get(value));
        }
    }

    public DefaultExpirationManager<V> copy() {
        return new DefaultExpirationManager<>(this.policyData.keySet().toArray(new ExpiringPolicy[0]));
    }

    public void onExpire(final V value) {
        for (final Consumer<V> globalExpireListener : this.globalExpireListeners) {
            globalExpireListener.accept(value);
        }

        for (final ExpiringPolicy<V, ?> policy : this.policyData.keySet()) {
            policy.onExpire(value);
        }
    }
}
