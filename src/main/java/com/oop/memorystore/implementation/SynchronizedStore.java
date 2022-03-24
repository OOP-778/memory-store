package com.oop.memorystore.implementation;

import com.oop.memorystore.api.Store;
import com.oop.memorystore.api.StoreQuery;
import com.oop.memorystore.implementation.index.*;
import com.oop.memorystore.implementation.index.reducer.Reducer;
import com.oop.memorystore.implementation.query.Query;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Synchronized (thread-safe) store backed by given store.
 *
 * @param <V> value type
 */
public class SynchronizedStore<V> implements Store<V> {
  protected final Store<V> store;
  protected final Object mutex;

  public SynchronizedStore(final Store<V> store) {
    this.store = store;
    this.mutex = this;
  }

  @Override
  public Index<V> getIndex(final String indexName) {
    final Index<V> index;

    synchronized (this.mutex) {
      final Index<V> found = this.store.getIndex(indexName);
      index = found == null ? null : new SynchronizedIndex<>(found, this.mutex);
    }

    return index;
  }

  @Override
  public Collection<Index<V>> getIndexes() {
    final List<Index<V>> indexes;

    synchronized (this.mutex) {
      indexes =
          this.store.getIndexes().stream()
              .map(index -> new SynchronizedIndex<>(index, this.mutex))
              .collect(Collectors.toList());
    }

    return indexes;
  }

  @Override
  public List<V> remove(final Query query, final int limit) {
    synchronized (this.mutex) {
      return this.store.remove(query, limit);
    }
  }

  @Override
  public void removeAllIndexes() {
    synchronized (this.mutex) {
        this.store.removeAllIndexes();
    }
  }

  @Override
  public Optional<Index<V>> findIndex(final String indexName) {
    final Optional<Index<V>> optionalIndex;

    synchronized (this.mutex) {
      optionalIndex =
          this.store.findIndex(indexName).map(index -> new SynchronizedIndex<>(index, this.mutex));
    }

    return optionalIndex;
  }

  @Override
  public boolean removeIndex(final Index<V> index) {
    final boolean removed;

    synchronized (this.mutex) {
      if (index instanceof SynchronizedIndex) {
        removed = this.store.removeIndex(((SynchronizedIndex<V>) index).getIndex());
      } else {
        removed = this.store.removeIndex(index);
      }
    }

    return removed;
  }

  @Override
  public boolean removeIndex(final String indexName) {
    final boolean removed;

    synchronized (this.mutex) {
      removed = this.store.removeIndex(indexName);
    }

    return removed;
  }

  @Override
  public <K> Index<V> index(final String indexName, final IndexDefinition<K, V> indexDefinition)
      throws IndexException {
    final Index<V> index;

    synchronized (this.mutex) {
      index = this.store.index(indexName, indexDefinition);
    }

    return index;
  }

  @Override
  public <K> Index<V> index(final IndexDefinition<K, V> indexDefinition) throws IndexException {
    final Index<V> index;

    synchronized (this.mutex) {
      index = this.store.index(indexDefinition);
    }

    return index;
  }

  @Override
  public <K> Index<V> index(final String indexName, final KeyMapper<K, V> keyMapper)
      throws IndexException {
    final Index<V> index;

    synchronized (this.mutex) {
      index = this.store.index(indexName, keyMapper);
    }

    return index;
  }

  @Override
  public <K> Index<V> index(final KeyMapper<K, V> keyMapper) throws IndexException {
    final Index<V> index;

    synchronized (this.mutex) {
      index = this.store.index(keyMapper);
    }

    return index;
  }

  @Override
  public <K> Index<V> index(
      final String indexName, final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer)
      throws IndexException {
    final Index<V> index;

    synchronized (this.mutex) {
      index = this.store.index(indexName, keyMapper, reducer);
    }

    return index;
  }

  @Override
  public <K> Index<V> index(final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer)
      throws IndexException {
    final Index<V> index;

    synchronized (this.mutex) {
      index = this.store.index(keyMapper, reducer);
    }

    return index;
  }

  @Override
  public List<V> get(final String indexName, final Object key, final int limit) {
    final List<V> results;

    synchronized (this.mutex) {
      results = this.store.get(indexName, key, limit);
    }

    return results;
  }

  @Override
  public List<V> get(final String indexName, final Object key) {
    final List<V> results;

    synchronized (this.mutex) {
      results = this.store.get(indexName, key);
    }

    return results;
  }

  @Override
  public V getFirst(final String indexName, final Object key) {
    final V result;

    synchronized (this.mutex) {
      result = this.store.getFirst(indexName, key);
    }

    return result;
  }

  @Override
  public Optional<V> findFirst(final String indexName, final Object key) {
    final Optional<V> result;

    synchronized (this.mutex) {
      result = this.store.findFirst(indexName, key);
    }

    return result;
  }

  @Override
  public List<V> get(final Query query, final int limit) {
    final List<V> results;

    synchronized (this.mutex) {
      results = this.store.get(query, limit);
    }

    return results;
  }

  @Override
  public List<V> get(final Query query) {
    final List<V> results;

    synchronized (this.mutex) {
      results = this.store.get(query);
    }

    return results;
  }

  @Override
  public V getFirst(final Query query) {
    final V result;

    synchronized (this.mutex) {
      result = this.store.getFirst(query);
    }

    return result;
  }

  @Override
  public Optional<V> findFirst(final Query query) {
    final Optional<V> result;

    synchronized (this.mutex) {
      result = this.store.findFirst(query);
    }

    return result;
  }

  @Override
  public void reindex() {
    synchronized (this.mutex) {
        this.store.reindex();
    }
  }

  @Override
  public void reindex(final Collection<V> items) {
    synchronized (this.mutex) {
        this.store.reindex(items);
    }
  }

  @Override
  public void reindex(final V item) {
    synchronized (this.mutex) {
        this.store.reindex(item);
    }
  }

  @Override
  public Store<V> copy() {
    final Store<V> copy;

    synchronized (this.mutex) {
      copy = this.store.copy();
    }

    return copy;
  }

  @Override
  public int size() {
    final int size;

    synchronized (this.mutex) {
      size = this.store.size();
    }

    return size;
  }

  @Override
  public boolean isEmpty() {
    final boolean empty;

    synchronized (this.mutex) {
      empty = this.store.isEmpty();
    }

    return empty;
  }

  @Override
  public boolean contains(final Object obj) {
    final boolean contains;

    synchronized (this.mutex) {
      contains = this.store.contains(obj);
    }

    return contains;
  }

  @Override
  public Iterator<V> iterator() {
    return this.store.iterator();
  }

  @Override
  public Object[] toArray() {
    final Object[] array;

    synchronized (this.mutex) {
      array = this.store.toArray();
    }

    return array;
  }

  @Override
  public <T1> T1[] toArray(final T1[] array) {
    final T1[] toArray;

    synchronized (this.mutex) {
      toArray = this.store.toArray(array);
    }

    return toArray;
  }

  @Override
  public boolean add(final V item) {
    final boolean result;

    synchronized (this.mutex) {
      result = this.store.add(item);
    }

    return result;
  }

  @Override
  public boolean remove(final Object obj) {
    final boolean result;

    synchronized (this.mutex) {
      result = this.store.remove(obj);
    }

    return result;
  }

  @Override
  public boolean containsAll(final Collection<?> collection) {
    final boolean result;

    synchronized (this.mutex) {
      result = this.store.containsAll(collection);
    }

    return result;
  }

  @Override
  public boolean addAll(final Collection<? extends V> collection) {
    final boolean result;

    synchronized (this.mutex) {
      result = this.store.addAll(collection);
    }

    return result;
  }

  @Override
  public boolean addAll(final V[] items) throws IndexException {
    final boolean result;

    synchronized (this.mutex) {
      result = this.store.addAll(items);
    }

    return result;
  }

  @Override
  public boolean removeAll(final Collection<?> collection) {
    final boolean result;

    synchronized (this.mutex) {
      result = this.store.removeAll(collection);
    }

    return result;
  }

  @Override
  public boolean removeIf(final Predicate<? super V> filter) {
    final boolean result;

    synchronized (this.mutex) {
      result = this.store.removeIf(filter);
    }

    return result;
  }

  @Override
  public boolean retainAll(final Collection<?> collection) {
    final boolean result;

    synchronized (this.mutex) {
      result = this.store.retainAll(collection);
    }

    return result;
  }

  @Override
  public void clear() {
    synchronized (this.mutex) {
        this.store.clear();
    }
  }

  public Store<V> getStore() {
    return this.store;
  }

  @Override
  public Store<V> synchronizedStore() {
    return this;
  }

  @Override
  public void lockIndexing(final boolean lockIndexing) {
    synchronized (this.mutex) {
        this.store.lockIndexing(lockIndexing);
    }
  }

  @Override
  public StoreQuery<V> createQuery() {
    return new StoreQueryImpl<>(this);
  }

  @Override
  public void printDetails(V value) {
    synchronized (this.mutex) {
      this.store.printDetails(value);
    }
  }

  @Override
  public String toString() {
    return this.store.toString();
  }
}
