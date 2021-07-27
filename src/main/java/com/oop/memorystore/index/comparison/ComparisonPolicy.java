package com.oop.memorystore.index.comparison;

/**
 * Transforms a given key into a comparable key.
 *
 * @param <T> type
 */
public interface ComparisonPolicy<T> {
  /**
   * Returns true if comparison is supported for this class type
   *
   * @param clazz class to check for support
   * @return true if supported
   */
  boolean supports(Class<?> clazz);

  /**
   * Transform the given item into the comparable type
   *
   * @param item item
   * @return comparable
   */
  T createComparable(T item);
}
