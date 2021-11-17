package com.oop.memorystore.implementation.expiring;

import com.oop.memorystore.api.ExpirationManager;
import com.oop.memorystore.api.ExpiringStore;
import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.AbstractStore;
import com.oop.memorystore.implementation.StoreQueryImpl;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ExpiringMemoryStore<V> extends AbstractStore<V> implements ExpiringStore<V> {
    private final DefaultExpirationManager<V> expirationManager;

    public ExpiringMemoryStore(final ExpiringPolicy<V, ?>... policies) {
        super(
            new DefaultReferenceManager<>(
                new DefaultIdentityProvider(), new ExpiringReferenceFactory<>(), Collections.synchronizedMap(new LinkedHashMap<>())),
            new ReferenceIndexManager<>());
        this.expirationManager = new DefaultExpirationManager<>(policies);
    }

    protected ExpiringMemoryStore(
        final ReferenceManager<V> referenceManager,
        final IndexManager<V> indexManager,
        final DefaultExpirationManager<V> defaultExpirationManager) {
        super(referenceManager, indexManager);
        this.expirationManager = defaultExpirationManager;
    }

    @Override
    public boolean add(final V item) {
        final boolean added = super.add(item);
        if (added) {
            this.expirationManager.onAdd(item);
        }

        return added;
    }

    @Override
    protected Store<V> createCopy(
        final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
        return new ExpiringMemoryStore<>(referenceManager.copy(), indexManager.copy(), this.expirationManager.copy());
    }

    @Override
    public StoreQueryImpl<V> createQuery() {
        return new ExpiringStoreQuery<>(this);
    }

    @Override
    public List<V> get(final Query query, final int limit) {
        final QueryDefinition definition = query.build();
        final List<IndexMatch> indexMatches = definition.getIndexMatches();
        final Operator operator = definition.getOperator();
        final Set<Reference<V>> results = new LinkedHashSet<>();

        boolean firstMatch = true;

        for (final IndexMatch indexMatch : indexMatches) {
            final ReferenceIndex<?, V> referenceIndex = this.indexManager.getIndex(indexMatch.getIndexName());
            if (referenceIndex == null) {
                continue;
            }

            final Set<Reference<V>> references =
                new HashSet<>(Optional.of(referenceIndex)
                    .map(index -> index.getReferences(indexMatch.getKey()))
                    .orElse(new HashSet<>()));

            references.removeIf(
                reference -> {
                    final boolean shouldBeInvalidated = this.expirationManager.checkExpiration(reference.get());
                    if (shouldBeInvalidated) {
                        this.invalidate(reference.get());
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
            .peek(reference -> this.expirationManager.onAccess(reference.get()))
            .map(Reference::get)
            .limit(limit == -1 ? Long.MAX_VALUE : limit)
            .collect(Collectors.toList());
    }

    @Override
    public ExpirationManager<V> getExpirationManager() {
        return this.expirationManager;
    }

    @Override
    public void invalidate() {
        for (final Reference<V> reference : this.referenceManager.getReferences()) {
            final MemoryReference<V> next = (MemoryReference<V>) reference;
            if (!this.expirationManager.checkExpiration(next.get())) {
                continue;
            }

            this.invalidate(next.get());
        }
    }

    @Override
    public boolean remove(final Object obj) {
        final boolean removed = super.remove(obj);
        if (removed) {
            this.expirationManager.onRemove((V) obj);
        }

        return removed;
    }

    @Override
    public Store<V> synchronizedStore() {
        return new SynchronizedExpiringStore<>(this);
    }

    public void invalidate(final V value) {
        this.expirationManager.onExpire(value);
        this.remove(value);
    }
}
