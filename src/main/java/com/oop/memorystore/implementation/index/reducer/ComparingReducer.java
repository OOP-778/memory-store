package com.oop.memorystore.implementation.index.reducer;

import com.oop.memorystore.implementation.index.Element;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

abstract class ComparingReducer<K, V> implements Reducer<K, V> {
  private final Function<V, ?> valueProvider;
  private final Comparator<Object> comparator;
  private final boolean nullGreater;

  @SuppressWarnings("unchecked")
  <C extends Comparable<? super C>> ComparingReducer(
      final Function<V, C> valueProvider, final boolean nullGreater) {
    this.valueProvider = valueProvider;
    this.comparator = Comparator.comparing(obj -> ((C) obj));
    this.nullGreater = nullGreater;
  }

  @SuppressWarnings("unchecked")
  <C> ComparingReducer(
      final Function<V, C> valueProvider,
      final Comparator<C> comparator,
      final boolean nullGreater) {
    this.valueProvider = valueProvider;
    this.comparator = (Comparator<Object>) comparator;
    this.nullGreater = nullGreater;
  }

  @Override
  public void reduce(final K key, final List<Element<V>> elements) {
    elements.stream().reduce(this::reduce);
  }

  private Element<V> reduce(final Element<V> element1, final Element<V> element2) {
    final Object comparable1 = valueProvider.apply(element1.get());
    final Object comparable2 = valueProvider.apply(element2.get());

    if (compare(comparable1, comparable2, comparator, nullGreater) > 0) {
      element2.remove();
      return element1;
    }

    element1.remove();
    return element2;
  }

  abstract int compare(
      Object value1, Object value2, Comparator<Object> comparator, boolean nullGreater);
}
