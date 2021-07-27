package com.oop.memorystore.expiring;

import com.oop.memorystore.AbstractStore;
import com.oop.memorystore.ExpiringStore;
import com.oop.memorystore.Store;
import com.oop.memorystore.identity.DefaultIdentityProvider;
import com.oop.memorystore.index.IndexManager;
import com.oop.memorystore.index.ReferenceIndex;
import com.oop.memorystore.index.ReferenceIndexManager;
import com.oop.memorystore.query.IndexMatch;
import com.oop.memorystore.query.Operator;
import com.oop.memorystore.query.Query;
import com.oop.memorystore.query.QueryDefinition;
import com.oop.memorystore.reference.DefaultReferenceManager;
import com.oop.memorystore.reference.Reference;
import com.oop.memorystore.reference.ReferenceManager;

import java.util.*;
import java.util.stream.Collectors;

public class ExpiringMemoryStore<V> extends AbstractStore<V> implements ExpiringStore<V> {

  private final ExpiringPolicy policy;

  public ExpiringMemoryStore(final ExpiringPolicy expiringPolicy) {
    super(
        new DefaultReferenceManager<>(
            new DefaultIdentityProvider(), new ExpiringReferenceFactory<>(expiringPolicy)),
        new ReferenceIndexManager<>());
    this.policy = expiringPolicy;
  }

  protected ExpiringMemoryStore(
      final ExpiringPolicy policy,
      ReferenceManager<V> referenceManager,
      IndexManager<V> indexManager) {
    super(referenceManager, indexManager);
    this.policy = policy;
  }

  @Override
  protected Store<V> createCopy(
      ReferenceManager<V> referenceManager, IndexManager<V> indexManager) {
    return new ExpiringMemoryStore<>(this.policy, referenceManager.copy(), indexManager.copy());
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
            boolean shouldBeInvalidated = ((ExpiringReference<V>) reference).shouldBeInvalidated();
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
        .peek(reference -> ((ExpiringReference<V>) reference).updateFetched())
        .map(Reference::get)
        .limit(limit == -1 ? Long.MAX_VALUE : limit)
        .collect(Collectors.toList());
  }

  @Override
  public ExpiringPolicy getExpiringPolicy() {
    return policy;
  }

  @Override
  public void invalidate() {
    Iterator<Reference<V>> iterator = referenceManager.getReferences().iterator();
    while (iterator.hasNext()) {
      ExpiringReference<V> next = (ExpiringReference<V>) iterator.next();
      if (!(next.shouldBeInvalidated())) continue;

      indexManager.removeReference(next);
      iterator.remove();
    }
  }

  @Override
  public Store<V> synchronizedStore() {
    return new SynchronizedExpiringStore<>(this);
  }
}
