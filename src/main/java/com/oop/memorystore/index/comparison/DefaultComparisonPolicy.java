package com.oop.memorystore.index.comparison;

/**
 * Apply default comparison logic
 *
 * @param <V> value type
 */
public class DefaultComparisonPolicy<V> implements ComparisonPolicy<V> {
  @Override
  public boolean supports(final Class<?> clazz) {
    return true;
  }

  @Override
  public V createComparable(final V item) {
    return item;
  }
}
