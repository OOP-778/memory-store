package com.oop.memorystore.implementation.expiring;

import com.oop.memorystore.api.ExpirationManager;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy.ExpirationData;
import com.sun.istack.internal.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class to handle expirations
 *
 * @param <V>
 */
public class DefaultExpirationManager<V> implements ExpirationManager<V> {
    private final Map<ExpiringPolicy<V, ?>, Map<V, ExpiringPolicy.ExpirationData>> policyData =
        new HashMap<>();

    @SafeVarargs
    public DefaultExpirationManager(final ExpiringPolicy<V, ?>... policies) {
        for (final ExpiringPolicy<V, ?> policy : policies) {
            this.policyData.put(policy, new HashMap<>());
        }
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
        for (final Map<V, ExpiringPolicy.ExpirationData> policyData : this.policyData.values()) {
            policyData.remove(value);
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

    public ExpirationData getExpirationData(final V value, @NotNull final Class<? extends ExpiringPolicy<?, ?>> policyClass) {
        for (final Entry<ExpiringPolicy<V, ?>, Map<V, ExpirationData>> policyEntry : this.policyData.entrySet()) {
            if (!policyClass.isAssignableFrom(policyEntry.getKey().getClass())) {
                continue;
            }

            return policyEntry.getValue().get(value);
        }

        return null;
    }

    public DefaultExpirationManager<V> copy() {
        return new DefaultExpirationManager<>(this.policyData.keySet().toArray(new ExpiringPolicy[0]));
    }
}
