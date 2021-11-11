package com.oop.memorystore.implementation.index.reducer;

import com.oop.memorystore.implementation.index.Element;

import java.util.List;

/**
 * Reduces elements a key once the configured limit has been reached
 *
 * @param <K> key type
 * @param <V> value type
 */
public class LimitReducer<K, V> implements Reducer<K, V> {
  private final int limit;
  private final Retain retain;

  public LimitReducer(final int limit, final Retain retain) {
    this.limit = limit;
    this.retain = retain;
  }

  @Override
  public void reduce(final K key, final List<Element<V>> elements) {
    if (elements.size() <= this.limit) {
      return;
    }

    if (this.retain == Retain.OLDEST) {
        this.reduceOldest(elements);
    } else if (this.retain == Retain.NEWEST) {
        this.reduceNewest(elements);
    }
  }

  private void reduceNewest(final List<Element<V>> elements) {
    for (int i = 0; i < (elements.size() - this.limit); i++) {
      elements.get(i).remove();
    }
  }

  private void reduceOldest(final List<Element<V>> elements) {
    for (int i = this.limit; i < elements.size(); i++) {
      elements.get(i).remove();
    }
  }

  public enum Retain {
    NEWEST,
    OLDEST
  }
}
