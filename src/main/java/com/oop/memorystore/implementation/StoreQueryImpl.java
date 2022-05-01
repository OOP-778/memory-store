package com.oop.memorystore.implementation;

import com.oop.memorystore.api.Store;
import com.oop.memorystore.api.StoreQuery;
import com.oop.memorystore.implementation.index.ReferenceIndex;
import com.oop.memorystore.implementation.query.QueryOperator;
import com.oop.memorystore.implementation.reference.Reference;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoreQueryImpl<V> implements StoreQuery<V> {
    protected final Store<V> store;
    protected final Set<Reference<V>> collection = new LinkedHashSet<>();

    public StoreQueryImpl(final Store<V> store) {
        this.store = store;
    }

    @Override
    public StoreQueryImpl<V> filter(final String indexName, final Object equals) {
        return this.filter(indexName, QueryOperator.FIRST, equals);
    }

    @Override
    public StoreQueryImpl<V> filter(final String indexName, final QueryOperator operator, final Object... equals) {
        final ReferenceIndex<?, V> index = this.store.getIndexManager().getIndex(indexName);
        if (index == null) {
            throw new IllegalStateException(String.format("Invalid index by name: %s", indexName));
        }

        final Set<Reference<V>> fetched = new LinkedHashSet<>();
        for (final Object equal : equals) {
            final Set<Reference<V>> values = this.getValuesOfIndex(index, equal);
            if (values.isEmpty()) {
                continue;
            }

            fetched.addAll(values);
            if (operator == QueryOperator.FIRST) {
                break;
            }
        }

        if (!this.collection.isEmpty()) {
            this.collection.retainAll(fetched);
        } else {
            this.collection.addAll(fetched);
        }
        return this;
    }

    @Override
    public Stream<V> asStream() {
        return this.collection
            .stream()
            .map(Reference::get);
    }

    @Override
    public <T extends Collection<V>> T collect(final T collection) {
        return this.asStream().collect(Collectors.toCollection(() -> collection));
    }

    @Override
    public Optional<V> first() {
        return this.collection
            .stream()
            .findFirst()
            .map(Reference::get);

    }

    protected LinkedHashSet<Reference<V>> getValuesOfIndex(final ReferenceIndex<?, V> index, final Object key) {
        return new LinkedHashSet<>(index.getReferences(key));
    }
}
