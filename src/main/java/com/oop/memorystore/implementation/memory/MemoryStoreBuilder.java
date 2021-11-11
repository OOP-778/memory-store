package com.oop.memorystore.implementation.memory;

import com.oop.memorystore.implementation.index.IndexDefinition;
import com.oop.memorystore.implementation.index.KeyMapper;
import com.oop.memorystore.implementation.index.reducer.Reducer;

import java.util.Collection;

/**
 * Builder for a memory store
 *
 * @param <V> value type
 */
public final class MemoryStoreBuilder<V> {
  private final MemoryStore<V> store;

  MemoryStoreBuilder() {
      this.store = new MemoryStore<>();
  }

  public final MemoryStoreBuilder<V> withValue(final V value) {
      this.store.add(value);
    return this;
  }

  public final MemoryStoreBuilder<V> withValues(final Collection<V> values) {
      this.store.addAll(values);
    return this;
  }

  @SafeVarargs
  public final MemoryStoreBuilder<V> withValues(final V... values) {
      this.store.addAll(values);
    return this;
  }

  public final <K> MemoryStoreBuilder<V> withIndex(
      final String indexName, final KeyMapper<K, V> keyMapper) {
      this.store.index(indexName, keyMapper);
    return this;
  }

  public final <K> MemoryStoreBuilder<V> withIndex(
      final String indexName, final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer) {
      this.store.index(indexName, keyMapper, reducer);
    return this;
  }

  public final <K> MemoryStoreBuilder<V> withIndex(
      final String indexName, final IndexDefinition<K, V> indexDefinition) {
      this.store.index(indexName, indexDefinition);
    return this;
  }

  public final MemoryStore<V> build() {
    return this.store;
  }
}
