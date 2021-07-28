package com.oop.memorystore.implementation.index;

/**
 * Map a value to a single indexed key
 *
 * @param <K> key type
 * @param <V> value type
 */
@FunctionalInterface
public interface KeyMapper<K, V> {
  /**
   * Index value to key or return null to skip indexing
   *
   * @param value value to transform into a key
   * @return key
   */
  K map(V value);
}
