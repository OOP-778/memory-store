package com.oop.memorystore.implementation.index;

import com.oop.memorystore.implementation.reference.Reference;

/**
 * A wrapper around an element in the data store. Only call {@link #get()} when necessary, depending
 * on the Store implementation this may result in an IO operation.
 *
 * @param <V> value type
 */
public final class Element<V> {
  private final Reference<V> reference;
  private boolean removed;

  public Element(final Reference<V> reference) {
    this.reference = reference;
  }

  /**
   * Get the value of the element
   *
   * @return element value
   */
  public V get() {
    return this.reference.get();
  }

  /** Remove this element */
  public void remove() {
      this.removed = true;
  }

  /**
   * Is removed
   *
   * @return removed
   */
  public boolean isRemoved() {
    return this.removed;
  }

  Reference<V> getReference() {
    return this.reference;
  }
}
