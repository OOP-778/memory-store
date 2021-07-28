package com.oop.memorystore.implementation.index.reducer;

import com.oop.memorystore.implementation.index.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper for multiple reducers that are triggered in order.
 *
 * @param <K> key
 * @param <V>value
 */
class MultiReducer<K, V> implements Reducer<K, V> {
  private final List<Reducer<K, V>> reducers;

  MultiReducer(final List<Reducer<K, V>> reducers) {
    this.reducers = new ArrayList<>(reducers);
  }

  @Override
  public void reduce(final K key, final List<Element<V>> elements) {
    for (final Reducer<K, V> reducer : reducers) {
      reducer.reduce(
          key,
          elements.stream().filter(element -> !element.isRemoved()).collect(Collectors.toList()));
    }
  }

  @Override
  public MultiReducer<K, V> andThen(final Reducer<K, V> reducer) {
    final List<Reducer<K, V>> updatedReducers = new ArrayList<>(reducers);
    updatedReducers.add(reducer);
    return new MultiReducer<>(updatedReducers);
  }

  public List<Reducer<K, V>> getReducers() {
    return Collections.unmodifiableList(reducers);
  }
}
