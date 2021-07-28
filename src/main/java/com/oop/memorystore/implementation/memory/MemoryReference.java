package com.oop.memorystore.implementation.memory;

import com.oop.memorystore.implementation.reference.Reference;

/**
 * Reference to a stored item in memory
 *
 * @param <V> reference type
 */
public class MemoryReference<V> implements Reference<V> {
  private final V reference;

  public MemoryReference(final V reference) {
    this.reference = reference;
  }

  @Override
  public V get() {
    return reference;
  }

  @Override
  public String toString() {
    return String.valueOf(reference);
  }
}
