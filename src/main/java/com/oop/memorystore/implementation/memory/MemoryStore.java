package com.oop.memorystore.implementation.memory;

import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.AbstractStore;
import com.oop.memorystore.implementation.identity.DefaultIdentityProvider;
import com.oop.memorystore.implementation.index.IndexManager;
import com.oop.memorystore.implementation.index.ReferenceIndexManager;
import com.oop.memorystore.implementation.reference.DefaultReferenceManager;
import com.oop.memorystore.implementation.reference.ReferenceManager;
import java.util.Collection;

/**
 * In memory implementation of a {@link Store}
 *
 * @param <V> type of item referenced
 */
public class MemoryStore<V> extends AbstractStore<V> {
    public MemoryStore(final Collection<V> items) {
        this();
        this.addAll(items);
    }

    public MemoryStore() {
        this(
            new DefaultReferenceManager<>(
                new DefaultIdentityProvider(), new MemoryReferenceFactory<>()),
            new ReferenceIndexManager<>()
        );
    }

    private MemoryStore(
        final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
        super(referenceManager, indexManager);
    }

    @SafeVarargs
    public MemoryStore(final V... items) {
        this();
        this.addAll(items);
    }

    /**
     * New store builder
     *
     * @param <V> data type
     * @return builder
     */
    public static <V> MemoryStoreBuilder<V> newStore() {
        return new MemoryStoreBuilder<>();
    }

    /**
     * New store builder. This store isn't strongly typed, this is the same as calling MemoryStore.&lt;String&gt;newStore()
     *
     * @param type data type
     * @param <V>  data type
     * @return builder
     */
    public static <V> MemoryStoreBuilder<V> newStore(final Class<V> type) {
        return new MemoryStoreBuilder<>();
    }

    @Override
    protected Store<V> createCopy(
        final ReferenceManager<V> referenceManager, final IndexManager<V> indexManager) {
        return new MemoryStore<>(referenceManager.copy(), indexManager.copy());
    }

    @Override
    public String toString() {
        return this.getReferenceManager().getReferences().toString();
    }
}
