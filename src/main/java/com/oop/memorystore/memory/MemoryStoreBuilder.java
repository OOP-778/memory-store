package com.oop.memorystore.memory;

import com.oop.memorystore.index.IndexDefinition;
import com.oop.memorystore.index.KeyMapper;
import com.oop.memorystore.index.reducer.Reducer;

import java.util.Collection;

/**
 * Builder for a memory store
 *
 * @param <V> value type
 */
public final class MemoryStoreBuilder<V> {
  private final MemoryStore<V> store;

  MemoryStoreBuilder() {
    store = new MemoryStore<>();
  }

  public final MemoryStoreBuilder<V> withValue(final V value) {
    store.add(value);
    return this;
  }

  public final MemoryStoreBuilder<V> withValues(final Collection<V> values) {
    store.addAll(values);
    return this;
  }

  @SafeVarargs
  public final MemoryStoreBuilder<V> withValues(final V... values) {
    store.addAll(values);
    return this;
  }

  public final <K> MemoryStoreBuilder<V> withIndex(
      final String indexName, final KeyMapper<K, V> keyMapper) {
    store.index(indexName, keyMapper);
    return this;
  }

  public final <K> MemoryStoreBuilder<V> withIndex(
      final String indexName, final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer) {
    store.index(indexName, keyMapper, reducer);
    return this;
  }

  public final <K> MemoryStoreBuilder<V> withIndex(
      final String indexName, final IndexDefinition<K, V> indexDefinition) {
    store.index(indexName, indexDefinition);
    return this;
  }

  public final MemoryStore<V> build() {
    return store;
  }
}
