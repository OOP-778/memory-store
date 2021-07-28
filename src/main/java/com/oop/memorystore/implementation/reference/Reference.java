package com.oop.memorystore.implementation.reference;

/**
 * Reference a stored item
 *
 * @param <V> type of item referenced
 */
public interface Reference<V> {
  /**
   * Get referenced item
   *
   * @return referenced item
   */
  V get();
}
