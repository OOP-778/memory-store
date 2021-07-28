package com.oop.memorystore.implementation.memory;

import com.oop.memorystore.implementation.reference.Reference;
import com.oop.memorystore.implementation.reference.ReferenceFactory;

/**
 * Factory for creating in memory references
 *
 * @param <V> value type
 */
public class MemoryReferenceFactory<V> implements ReferenceFactory<V> {
  @Override
  public Reference<V> createReference(final V obj) {
    return new MemoryReference<>(obj);
  }
}
