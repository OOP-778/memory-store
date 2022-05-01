package com.oop.memorystore.implementation;

import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.index.Index;
import com.oop.memorystore.implementation.index.IndexDefinition;
import com.oop.memorystore.implementation.index.IndexException;
import com.oop.memorystore.implementation.index.IndexManager;
import com.oop.memorystore.implementation.index.ReferenceIndex;
import com.oop.memorystore.implementation.query.IndexMatch;
import com.oop.memorystore.implementation.query.Operator;
import com.oop.memorystore.implementation.query.Query;
import com.oop.memorystore.implementation.query.QueryDefinition;
import com.oop.memorystore.implementation.reference.Reference;
import com.oop.memorystore.implementation.reference.ReferenceManager;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractStore<V> extends AbstractCollection<V> implements Store<V> {
    protected final ReferenceManager<V> referenceManager;
    protected final IndexManager<V> indexManager;

    protected boolean lockIndexing = false;

    protected AbstractStore(final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
        this.referenceManager = referenceManager;
        this.indexManager = indexManager;
    }

    @Override
    public <K> Index<V> index(final String indexName, final IndexDefinition<K, V> indexDefinition)
        throws IndexException {
        return this.indexManager.createIndex(indexName, indexDefinition, this.referenceManager.getReferences());
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
                Optional.ofNullable(this.indexManager.getIndex(indexMatch.getIndexName()))
                    .map(index -> index.getReferences(indexMatch.getKey()))
                    .orElse(Collections.emptySet());

            if (firstMatch || operator == Operator.OR) {
                results.addAll(references);
                firstMatch = false;
            } else {
                results.retainAll(references);
            }
        }

        return results
            .stream()
            .map(Reference::get)
            .limit(limit == -1 ? Long.MAX_VALUE : limit)
            .collect(Collectors.toList());
    }

    @Override
    public Index<V> getIndex(final String indexName) {
        return this.indexManager.getIndex(indexName);
    }

    @Override
    public Collection<Index<V>> getIndexes() {
        return this.indexManager.getIndexes();
    }

    public List<V> remove(final Query query, int limit) {
        final QueryDefinition definition = query.build();
        final List<IndexMatch> indexMatches = definition.getIndexMatches();

        final List<V> result = new LinkedList<>();

        for (final IndexMatch indexMatch : indexMatches) {
            if (limit == 0) {
                break;
            }

            final Set<Reference<V>> references =
                Optional.ofNullable(this.indexManager.getIndex(indexMatch.getIndexName()))
                    .map(index -> index.getReferences(indexMatch.getKey()))
                    .orElse(new HashSet<>());

            final List<V> removed =
                references
                    .stream()
                    .limit(limit == -1 ? Long.MAX_VALUE : limit)
                    .peek(this.indexManager::removeReference)
                    .map(Reference::get)
                    .peek(this.referenceManager::remove)
                    .collect(Collectors.toList());

            result.addAll(removed);
            if (limit != -1) {
                limit -= removed.size();
            }
        }

        return result;
    }

    @Override
    public boolean removeIndex(final String indexName) {
        return this.indexManager.removeIndex(indexName);
    }

    @Override
    public boolean removeIndex(final Index<V> index) {
        return this.indexManager.removeIndex(index);
    }

    @Override
    public void reindex() {
        this.indexManager.reindex(this.referenceManager.getReferences());
    }

    @Override
    public void reindex(final Collection<V> items) {
        final List<Reference<V>> references =
            items
                .stream()
                .map(this.referenceManager::findReference)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        this.indexManager.reindex(references);
    }

    @Override
    public void reindex(final V item) {
        this.reindex(Collections.singleton(item));
    }

    @Override
    public Store<V> copy() {
        return this.createCopy(this.referenceManager, this.indexManager);
    }

    public void lockIndexing(final boolean lockIndexing) {
        this.lockIndexing = lockIndexing;
    }

    public StoreQueryImpl<V> createQuery() {
        return new StoreQueryImpl<>(this);
    }

    protected abstract Store<V> createCopy(
        final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager);

    @Override
    public Iterator<V> iterator() {
        return new StoreIterator(this.referenceManager.getReferences().iterator());
    }

    @Override
    public int size() {
        return this.referenceManager.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean contains(final Object obj) {
        return this.referenceManager.findReference(obj).isPresent();
    }

    @Override
    public boolean add(final V item) {
        return this.addAll(Collections.singleton(item));
    }

    @Override
    public boolean remove(final Object obj) {
        final Reference<V> reference = this.referenceManager.remove(obj);

        if (reference != null) {
            this.indexManager.removeReference(reference);
            return true;
        }

        return false;
    }

    @Override
    public boolean addAll(final Collection<? extends V> collection) {
        final List<Reference<V>> references = new ArrayList<>();
        boolean changed = false;

        for (final V item : collection) {
            final Optional<Reference<V>> existingReference = this.referenceManager.findReference(item);

            if (existingReference.isPresent()) {
                references.add(existingReference.get());
                continue;
            }

            references.add(this.referenceManager.add(item));
            changed = true;
        }

        if (!this.lockIndexing) {
            this.indexManager.reindex(references);
        }
        return changed;
    }

    @Override
    public void clear() {
        this.referenceManager.clear();
        this.indexManager.clear();
    }

    protected ReferenceManager<V> getReferenceManager() {
        return this.referenceManager;
    }

    private class StoreIterator implements Iterator<V> {
        private final Iterator<Reference<V>> iterator;
        private Reference<V> previous;

        StoreIterator(final Iterator<Reference<V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public V next() {
            this.previous = this.iterator.next();
            return this.previous.get();
        }

        @Override
        public void remove() {
            this.iterator.remove();
            AbstractStore.this.indexManager.removeReference(this.previous);
        }
    }

    @Override
    public void printDetails(V value) {
        System.out.println(String.format("=== Printing Details about %s ==", value));

        final Optional<Reference<V>> reference = this.getReferenceManager().findReference(value);
        if (!reference.isPresent()) {
            System.out.println("This value does not exist in the collection");
            return;
        }

        final Map<String, Collection> indexData = this.indexManager.getIndexData(reference.get());
        System.out.println("=== Indexes ===");
        for (Entry<String, Collection> indexEntry : indexData.entrySet()) {
            System.out.println(String.format("Index: %s, keys: %s", indexEntry.getKey(), indexEntry.getValue()));
        }
    }

    @Override
    public IndexManager<V> getIndexManager() {
        return this.indexManager;
    }
}
