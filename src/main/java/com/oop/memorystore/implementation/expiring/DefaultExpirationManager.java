package com.oop.memorystore.implementation.expiring;

import com.oop.memorystore.api.ExpirationManager;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle expirations
 *
 * @param <V>
 */
public class DefaultExpirationManager<V> implements ExpirationManager<V> {

  private final Map<ExpiringPolicy<V, ?>, Map<V, ExpiringPolicy.ExpirationData>> policyData =
      new HashMap<>();

  public DefaultExpirationManager(ExpiringPolicy<V, ?>... policies) {
    for (ExpiringPolicy<V, ?> policy : policies) {
      policyData.put(policy, new HashMap<>());
    }
  }

  public void onAdd(final V value) {
    for (Map.Entry<ExpiringPolicy<V, ?>, Map<V, ExpiringPolicy.ExpirationData>> policyEntry :
        policyData.entrySet()) {
      ExpiringPolicy.ExpirationData expirationData =
          policyEntry.getKey().createExpirationData(value);
      if (expirationData == null) {
        continue;
      }

      policyEntry.getValue().put(value, expirationData);
    }
  }

  public void onRemove(final V value) {
    for (Map<V, ExpiringPolicy.ExpirationData> policyData : policyData.values()) {
      policyData.remove(value);
    }
  }

  public void onAccess(final V value) {

  }

  public boolean checkExpiration(V value) {
    boolean shouldExpire = false;
    for (Map.Entry<
            ExpiringPolicy<V, ? extends ExpiringPolicy.ExpirationData>,
            Map<V, ExpiringPolicy.ExpirationData>>
        policyEntry : policyData.entrySet()) {
      final ExpiringPolicy policy = policyEntry.getKey();

      shouldExpire = policy.checkExpiration(value, policyEntry.getValue().get(value));
      if (shouldExpire) {
        break;
      }
    }

    return shouldExpire;
  }

  public DefaultExpirationManager<V> copy() {
    return new DefaultExpirationManager<>(policyData.keySet().toArray(new ExpiringPolicy[0]));
  }
}
