package com.oop.memorystore.implementation.index.reducer;

import com.oop.memorystore.implementation.index.Element;

import java.util.Arrays;
import java.util.List;

/**
 * Reduce all values matching the same key
 *
 * @param <K> key
 * @param <V> value
 */
@FunctionalInterface
public interface Reducer<K, V> {
  /**
   * Reduce values matched for the same key
   *
   * @param key key for values
   * @param elements elements to reduce
   */
  void reduce(K key, List<Element<V>> elements);

  /**
   * Chain two reducers together
   *
   * @param reducer reducer
   * @return chained reducer
   */
  default Reducer<K, V> andThen(final Reducer<K, V> reducer) {
    return new MultiReducer<>(Arrays.asList(this, reducer));
  }
}
