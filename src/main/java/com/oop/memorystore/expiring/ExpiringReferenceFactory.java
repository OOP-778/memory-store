package com.oop.memorystore.expiring;

import com.oop.memorystore.reference.Reference;
import com.oop.memorystore.reference.ReferenceFactory;

/**
 * Factory for creating expiring references
 *
 * @param <V> value type
 */
public class ExpiringReferenceFactory<V> implements ReferenceFactory<V> {

  private final ExpiringPolicy policy;

  public ExpiringReferenceFactory(final ExpiringPolicy policy) {
    this.policy = policy;
  }

  @Override
  public Reference<V> createReference(V obj) {
    return new ExpiringReference<>(obj, policy);
  }
}
