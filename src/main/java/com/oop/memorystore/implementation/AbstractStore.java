package com.oop.memorystore.implementation;

import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.index.Index;
import com.oop.memorystore.implementation.index.IndexDefinition;
import com.oop.memorystore.implementation.index.IndexException;
import com.oop.memorystore.implementation.index.IndexManager;
import com.oop.memorystore.implementation.query.IndexMatch;
import com.oop.memorystore.implementation.query.Operator;
import com.oop.memorystore.implementation.query.Query;
import com.oop.memorystore.implementation.query.QueryDefinition;
import com.oop.memorystore.implementation.reference.Reference;
import com.oop.memorystore.implementation.reference.ReferenceManager;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractStore<V> extends AbstractCollection<V> implements Store<V> {
  protected final ReferenceManager<V> referenceManager;
  protected final IndexManager<V> indexManager;

  protected boolean lockIndexing = false;

  protected AbstractStore(
      final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
    this.referenceManager = referenceManager;
    this.indexManager = indexManager;
  }

  public List<V> remove(final Query query, int limit) {
    final QueryDefinition definition = query.build();
    final List<IndexMatch> indexMatches = definition.getIndexMatches();

    final List<V> result = new LinkedList<>();

    for (final IndexMatch indexMatch : indexMatches) {
      if (limit == 0) break;

      final Set<Reference<V>> references =
          Optional.ofNullable(indexManager.getIndex(indexMatch.getIndexName()))
              .map(index -> index.getReferences(indexMatch.getKey()))
              .orElse(new HashSet<>());

      List<V> removed =
          references.stream()
              .limit(limit == -1 ? Long.MAX_VALUE : limit)
              .peek(indexManager::removeReference)
              .map(Reference::get)
              .collect(Collectors.toList());

      result.addAll(removed);
      limit -= removed.size();
    }

    return result;
  }

  @Override
  public List<V> get(final Query query, final int limit) {
    final QueryDefinition definition = query.build();
    final List<IndexMatch> indexMatches = definition.getIndexMatches();
    final Operator operator = definition.getOperator();
    final Set<Reference<V>> results = new LinkedHashSet<>();

    boolean firstMatch = true;

    for (final IndexMatch indexMatch : indexMatches) {
      final Set<Reference<V>> references =
          Optional.ofNullable(indexManager.getIndex(indexMatch.getIndexName()))
              .map(index -> index.getReferences(indexMatch.getKey()))
              .orElse(Collections.emptySet());

      if (firstMatch || operator == Operator.OR) {
        results.addAll(references);
        firstMatch = false;
      } else {
        results.retainAll(references);
      }
    }

    return results.stream()
        .map(Reference::get)
        .limit(limit == -1 ? Long.MAX_VALUE : limit)
        .collect(Collectors.toList());
  }

  @Override
  public <K> Index<V> index(final String indexName, final IndexDefinition<K, V> indexDefinition)
      throws IndexException {
    return indexManager.createIndex(indexName, indexDefinition, referenceManager.getReferences());
  }

  @Override
  public Index<V> getIndex(final String indexName) {
    return indexManager.getIndex(indexName);
  }

  @Override
  public Collection<Index<V>> getIndexes() {
    return indexManager.getIndexes();
  }

  @Override
  public boolean removeIndex(final Index<V> index) {
    return indexManager.removeIndex(index);
  }

  @Override
  public boolean removeIndex(final String indexName) {
    return indexManager.removeIndex(indexName);
  }

  @Override
  public void reindex() {
    indexManager.reindex(referenceManager.getReferences());
  }

  @Override
  public void reindex(final V item) {
    reindex(Collections.singleton(item));
  }

  @Override
  public void reindex(final Collection<V> items) {
    final List<Reference<V>> references =
        items.stream()
            .map(referenceManager::findReference)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    indexManager.reindex(references);
  }

  @Override
  public int size() {
    return referenceManager.size();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean contains(final Object obj) {
    return referenceManager.findReference(obj).isPresent();
  }

  @Override
  public Iterator<V> iterator() {
    return new StoreIterator(referenceManager.getReferences().iterator());
  }

  @Override
  public boolean addAll(final Collection<? extends V> collection) {
    final List<Reference<V>> references = new ArrayList<>();
    boolean changed = false;

    for (final V item : collection) {
      final Optional<Reference<V>> existingReference = referenceManager.findReference(item);

      if (existingReference.isPresent()) {
        references.add(existingReference.get());
        continue;
      }

      references.add(referenceManager.add(item));
      changed = true;
    }

    if (!lockIndexing) {
      indexManager.reindex(references);
    }
    return changed;
  }

  @Override
  public boolean add(final V item) {
    return addAll(Collections.singleton(item));
  }

  @Override
  public boolean remove(final Object obj) {
    final Reference<V> reference = referenceManager.remove(obj);

    if (reference != null) {
      indexManager.removeReference(reference);
      return true;
    }

    return false;
  }

  @Override
  public void clear() {
    referenceManager.clear();
    indexManager.clear();
  }

  @Override
  public Store<V> copy() {
    return createCopy(referenceManager, indexManager);
  }

  protected abstract Store<V> createCopy(
      final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager);

  protected ReferenceManager<V> getReferenceManager() {
    return referenceManager;
  }

  private class StoreIterator implements Iterator<V> {
    private final Iterator<Reference<V>> iterator;
    private Reference<V> previous;

    StoreIterator(final Iterator<Reference<V>> iterator) {
      this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public V next() {
      previous = iterator.next();
      return previous.get();
    }

    @Override
    public void remove() {
      iterator.remove();
      indexManager.removeReference(previous);
    }
  }

  public void lockIndexing(boolean lockIndexing) {
    this.lockIndexing = lockIndexing;
  }
}
