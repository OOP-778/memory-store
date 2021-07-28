package com.oop.memorystore.implementation.expiring;

import com.oop.memorystore.api.ExpiringStore;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy;
import com.oop.memorystore.implementation.memory.MemoryReference;
import com.oop.memorystore.implementation.reference.Reference;
import com.oop.memorystore.implementation.reference.ReferenceFactory;

/**
 * Factory for creating expiring references
 *
 * @param <V> value type
 */
public class ExpiringReferenceFactory<V> implements ReferenceFactory<V> {

  private final ExpiringStore<V> store;

  public ExpiringReferenceFactory(ExpiringStore<V> store) {
    this.store = store;
  }

  @Override
  public Reference<V> createReference(V obj) {
    store.getExpirationManager().onAdd(obj);
    return new MemoryReference<>(obj);
  }
}
