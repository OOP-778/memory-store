package com.oop.memorystore.index;

import com.oop.memorystore.index.comparison.ComparisonPolicy;
import com.oop.memorystore.index.reducer.Reducer;
import com.oop.memorystore.reference.Reference;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Maintains indexes against references to stored items
 *
 * @param <V> value type
 */
public class ReferenceIndex<K, V> implements Index<V> {
  private final String name;
  private final KeyMapper<Collection<K>, V> keyMapper;
  private final Reducer<K, V> reducer;
  private final ComparisonPolicy<K> comparisonPolicy;
  private final Map<K, References<K, V>> keyToReferencesMap;
  private final Map<Reference<V>, Set<K>> referenceToKeysMap;

  private ReferenceIndex(
      final String name,
      final KeyMapper<Collection<K>, V> keyMapper,
      final Reducer<K, V> reducer,
      final ComparisonPolicy<K> comparisonPolicy,
      final Map<K, References<K, V>> keyToReferencesMap,
      final Map<Reference<V>, Set<K>> referenceToKeysMap) {
    this.name = name;
    this.keyMapper = keyMapper;
    this.reducer = reducer;
    this.comparisonPolicy = comparisonPolicy;
    this.keyToReferencesMap = keyToReferencesMap;
    this.referenceToKeysMap = referenceToKeysMap;
  }

  public ReferenceIndex(
      final String indexName,
      final KeyMapper<Collection<K>, V> keyMapper,
      final Reducer<K, V> reducer,
      final ComparisonPolicy<K> comparisonPolicy) {
    this(indexName, keyMapper, reducer, comparisonPolicy, new HashMap<>(), new HashMap<>());
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Optional<V> findFirst(final Object key) {
    final K comparableKey = getComparableKey(key);
    final References<K, V> references = keyToReferencesMap.get(comparableKey);

    if (references == null) {
      return Optional.empty();
    }

    return references.findFirst();
  }

  public Set<Reference<V>> getReferences(final Object key) {
    final K comparableKey = getComparableKey(key);
    final References<K, V> references = keyToReferencesMap.get(comparableKey);

    if (references == null) {
      return Collections.emptySet();
    }

    return references.getAllReferences();
  }

  @Override
  public List<V> get(final Object key) {
    final K comparableKey = getComparableKey(key);
    final References<K, V> references = keyToReferencesMap.get(comparableKey);

    if (references == null) {
      return Collections.emptyList();
    }

    return references.getAll();
  }

  public void index(final Reference<V> reference) throws IndexCreationException {
    final Set<K> keys = generateKeys(reference);

    removeIndex(reference);

    if (!keys.isEmpty()) {
      referenceToKeysMap.put(reference, Collections.unmodifiableSet(keys));
      keys.forEach(
          key ->
              keyToReferencesMap
                  .computeIfAbsent(key, ignore -> new References<>(key, reference, reducer))
                  .add(reference));
    }
  }

  public void removeIndex(final Reference<V> reference) {
    final Set<K> keys = referenceToKeysMap.get(reference);

    if (keys == null) {
      return;
    }

    for (final K key : keys) {
      final References<K, V> references = keyToReferencesMap.get(key);

      if (reference != null) {
        references.remove(reference);

        if (references.isEmpty()) {
          keyToReferencesMap.remove(key);
        }
      }
    }

    referenceToKeysMap.remove(reference);
  }

  public void clear() {
    keyToReferencesMap.clear();
    referenceToKeysMap.clear();
  }

  public ReferenceIndex<K, V> copy() {
    final Map<K, References<K, V>> keyToReferencesMapCopy =
        keyToReferencesMap.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().copy()));

    final Map<Reference<V>, Set<K>> referenceToKeysMapCopy = new HashMap<>(referenceToKeysMap);

    return new ReferenceIndex<>(
        name, keyMapper, reducer, comparisonPolicy, keyToReferencesMapCopy, referenceToKeysMapCopy);
  }

  private Set<K> generateKeys(final Reference<V> reference) throws IndexCreationException {
    final V item;

    try {
      item = reference.get();
    } catch (final RuntimeException e) {
      throw new IndexCreationException("Index: " + name + ". Unable to retrieve item to index", e);
    }

    try {
      return keyMapper.map(item).stream()
          .map(this::getComparableKey)
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());
    } catch (final RuntimeException e) {
      throw new IndexCreationException(
          "Index: " + name + ". Error generating indexes for item: " + item, e);
    }
  }

  private K getComparableKey(final Object key) {
    if (key == null || !comparisonPolicy.supports(key.getClass())) {
      return null;
    }

    @SuppressWarnings("unchecked")
    final K comparable = comparisonPolicy.createComparable((K) key);
    return comparable;
  }

  @Override
  public String toString() {
    return "Index[name='" + name + "']";
  }
}
