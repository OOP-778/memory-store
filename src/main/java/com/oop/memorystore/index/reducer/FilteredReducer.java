package com.oop.memorystore.index.reducer;

import com.oop.memorystore.index.Element;

import java.util.List;
import java.util.function.Predicate;

/**
 * If the predicate return true, the element will be removed from the index
 *
 * @param <K> key type
 * @param <V> value type
 */
public class FilteredReducer<K, V> implements Reducer<K, V> {
  private final Predicate<V> predicate;

  public FilteredReducer(final Predicate<V> predicate) {
    this.predicate = predicate;
  }

  @Override
  public void reduce(final K key, final List<Element<V>> elements) {
    for (final Element<V> element : elements) {
      if (predicate.test(element.get())) {
        element.remove();
      }
    }
  }
}
