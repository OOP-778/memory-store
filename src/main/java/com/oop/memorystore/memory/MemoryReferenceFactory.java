package com.oop.memorystore.memory;

import com.oop.memorystore.reference.Reference;
import com.oop.memorystore.reference.ReferenceFactory;

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
