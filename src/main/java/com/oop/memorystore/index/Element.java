package com.oop.memorystore.index;

import com.oop.memorystore.reference.Reference;

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
    return reference.get();
  }

  /** Remove this element */
  public void remove() {
    removed = true;
  }

  /**
   * Is removed
   *
   * @return removed
   */
  public boolean isRemoved() {
    return removed;
  }

  Reference<V> getReference() {
    return reference;
  }
}
