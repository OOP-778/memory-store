package com.oop.memorystore.implementation.expiring;

import com.oop.memorystore.api.ExpirationManager;
import com.oop.memorystore.api.ExpiringStore;
import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.AbstractStore;
import com.oop.memorystore.implementation.expiring.policy.ExpiringPolicy;
import com.oop.memorystore.implementation.identity.DefaultIdentityProvider;
import com.oop.memorystore.implementation.index.IndexManager;
import com.oop.memorystore.implementation.index.ReferenceIndex;
import com.oop.memorystore.implementation.index.ReferenceIndexManager;
import com.oop.memorystore.implementation.memory.MemoryReference;
import com.oop.memorystore.implementation.query.IndexMatch;
import com.oop.memorystore.implementation.query.Operator;
import com.oop.memorystore.implementation.query.Query;
import com.oop.memorystore.implementation.query.QueryDefinition;
import com.oop.memorystore.implementation.reference.DefaultReferenceManager;
import com.oop.memorystore.implementation.reference.Reference;
import com.oop.memorystore.implementation.reference.ReferenceManager;

import java.util.*;
import java.util.stream.Collectors;

public class ExpiringMemoryStore<V> extends AbstractStore<V> implements ExpiringStore<V>{

  private final DefaultExpirationManager<V> expirationManager;

  public ExpiringMemoryStore(ExpiringPolicy<V, ?> ...policies) {
    super(
        new DefaultReferenceManager<>(
            new DefaultIdentityProvider(), new ExpiringReferenceFactory<>()),
        new ReferenceIndexManager<>());
    this.expirationManager = new DefaultExpirationManager<>(policies);
  }

  protected ExpiringMemoryStore(
      ReferenceManager<V> referenceManager,
      IndexManager<V> indexManager,
      DefaultExpirationManager<V> defaultExpirationManager) {
    super(referenceManager, indexManager);
    this.expirationManager = defaultExpirationManager;
  }

  @Override
  protected Store<V> createCopy(
      ReferenceManager<V> referenceManager, IndexManager<V> indexManager) {
    return new ExpiringMemoryStore<>(referenceManager.copy(), indexManager.copy(), expirationManager.copy());
  }

  @Override
  public List<V> get(final Query query, final int limit) {
    final QueryDefinition definition = query.build();
    final List<IndexMatch> indexMatches = definition.getIndexMatches();
    final Operator operator = definition.getOperator();
    final Set<Reference<V>> results = new LinkedHashSet<>();

    boolean firstMatch = true;

    for (final IndexMatch indexMatch : indexMatches) {
      final ReferenceIndex<?, V> referenceIndex = indexManager.getIndex(indexMatch.getIndexName());
      if (referenceIndex == null) continue;

      final Set<Reference<V>> references =
          Optional.of(referenceIndex)
              .map(index -> index.getReferences(indexMatch.getKey()))
              .orElse(Collections.emptySet());

      references.removeIf(
          reference -> {
            boolean shouldBeInvalidated = expirationManager.checkExpiration(reference.get());
            if (shouldBeInvalidated) {
              referenceIndex.removeIndex(reference);
            }

            return shouldBeInvalidated;
          });

      if (firstMatch || operator == Operator.OR) {
        results.addAll(references);
        firstMatch = false;
      } else {
        results.retainAll(references);
      }
    }

    return results.stream()
        .peek(reference -> expirationManager.onAccess(reference.get()))
        .map(Reference::get)
        .limit(limit == -1 ? Long.MAX_VALUE : limit)
        .collect(Collectors.toList());
  }

  @Override
  public ExpirationManager<V> getExpirationManager() {
    return expirationManager;
  }

  @Override
  public void invalidate() {
    Iterator<Reference<V>> iterator = referenceManager.getReferences().iterator();
    while (iterator.hasNext()) {
      MemoryReference<V> next = (MemoryReference<V>) iterator.next();
      if (!expirationManager.checkExpiration(next.get())) {
        continue;
      }

      indexManager.removeReference(next);
      iterator.remove();
    }
  }

  @Override
  public Store<V> synchronizedStore() {
    return new SynchronizedExpiringStore<>(this);
  }
}
