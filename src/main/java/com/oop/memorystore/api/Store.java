package com.oop.memorystore.api;

import com.oop.memorystore.implementation.SynchronizedStore;
import com.oop.memorystore.implementation.UnmodifiableStore;
import com.oop.memorystore.implementation.index.Index;
import com.oop.memorystore.implementation.index.IndexDefinition;
import com.oop.memorystore.implementation.index.IndexException;
import com.oop.memorystore.implementation.index.KeyMapper;
import com.oop.memorystore.implementation.index.reducer.Reducer;
import com.oop.memorystore.implementation.query.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Store of data
 *
 * @param <V> value type
 */
public interface Store<V> extends Collection<V> {
    /**
     * Adds an item to the store and indexes it. If the item already exists in the store, it will be reindexed.
     *
     * @param item item to on
     * @return true if item did not previously exist in the store.
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    @Override
    boolean add(V item) throws IndexException;

    /**
     * Adds all item to the store and indexes them. If an item already exists in the store, it will be reindexed.
     *
     * @param items items to on
     * @return true if one or more items did not previously exist in the store.
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    @Override
    boolean addAll(Collection<? extends V> items) throws IndexException;

    /**
     * Create an index using the index build provided
     *
     * @param indexName       name of the index
     * @param indexDefinition build of the index
     * @param <K>             indexed key type
     * @return index
     * @throws IndexException thrown if the creation of the new index fails with exceptions.
     */
    <K> Index<V> index(String indexName, IndexDefinition<K, V> indexDefinition) throws IndexException;

    /**
     * Create an index using the index build provided
     *
     * @param indexDefinition build of the index
     * @param <K>             indexed key type
     * @return index
     * @throws IndexException thrown if the creation of the new index fails with exceptions.
     */
    default <K> Index<V> index(final IndexDefinition<K, V> indexDefinition) throws IndexException {
        return this.index(UUID.randomUUID().toString(), indexDefinition);
    }

    /**
     * Register a new index with this store, mapping a single value to a collection of indexed keys. This is a convenient of creating an
     * index, for more options see {@link #index(IndexDefinition)}
     *
     * @param indexName name of the index
     * @param keyMapper function to provide a value to one or more keys
     * @param <K>       key type
     * @return index
     * @throws IndexException thrown if the new index failed with exceptions.
     */
    default <K> Index<V> index(final String indexName, final KeyMapper<K, V> keyMapper)
        throws IndexException {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper));
    }

    /**
     * Register a new index with this store, mapping a single value to a collection of indexed keys. This is a convenient of creating an
     * index, for more options see {@link #index(IndexDefinition)}
     *
     * @param indexName name of the index
     * @param keyMapper function to provide a value to one or more keys
     * @param reducer   function to reduce indexed values
     * @param <K>       key type
     * @return index
     * @throws IndexException thrown if the new index failed with exceptions.
     */
    default <K> Index<V> index(
        final String indexName, final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer)
        throws IndexException {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper).withReducer(reducer));
    }

    /**
     * Register a new index with this store, mapping a single value to a collection of indexed keys. This is a convenient of creating an
     * index, for more options see {@link #index(IndexDefinition)}
     *
     * @param keyMapper function to provide a value to one or more keys
     * @param reducer   function to reduce indexed values
     * @param <K>       key type
     * @return index
     * @throws IndexException thrown if the new index failed with exceptions.
     */
    default <K> Index<V> index(final KeyMapper<K, V> keyMapper, final Reducer<K, V> reducer)
        throws IndexException {
        return this.index(IndexDefinition.withKeyMapping(keyMapper).withReducer(reducer));
    }

    /**
     * Register a new index with this store, mapping a single value to a collection of indexed keys. This is a convenient of creating an
     * index, for more options see {@link #index(IndexDefinition)}
     *
     * @param keyMapper function to provide a value to one or more keys
     * @param <K>       key type
     * @return index
     * @throws IndexException thrown if the new index failed with exceptions.
     */
    default <K> Index<V> index(final KeyMapper<K, V> keyMapper) throws IndexException {
        return this.index(IndexDefinition.withKeyMapping(keyMapper));
    }

    /**
     * Query an index by name and lookup the given key. This method is the equivalent of calling {@link #getIndex(String)} and then {@link
     * Index#get(Object)}
     *
     * @param indexName name of the index to query
     * @param key       key to lookup
     * @param limit     limit results to the first x number of items
     * @return values associated with this key or an empty list
     */
    default List<V> get(final String indexName, final Object key, final int limit) {
        return this.get(Query.where(indexName, key), limit);
    }

    /**
     * Query an index by name and lookup the given key. This method is the equivalent of calling {@link #getIndex(String)} and then {@link
     * Index#get(Object)}
     *
     * @param indexName name of the index to query
     * @param key       key to lookup
     * @return values associated with this key or an empty list
     */
    default List<V> get(final String indexName, final Object key) {
        return this.get(Query.where(indexName, key), -1);
    }

    /**
     * Query an index by name and lookup the given key. This method is the equivalent of calling {@link #getIndex(String)} and then {@link
     * Index#getFirst(Object)}
     *
     * @param indexName name of the index to query
     * @param key       key to lookup
     * @return first value associated with this key or null
     */
    default V getFirst(final String indexName, final Object key) {
        return this.getFirst(Query.where(indexName, key));
    }

    /**
     * Query an index by name and lookup the given key. This method is the equivalent of calling {@link #getIndex(String)} and then {@link
     * Index#findFirst(Object)}
     *
     * @param indexName name of the index to query
     * @param key       key to lookup
     * @return first value associated with this key or an empty optional
     */
    default Optional<V> findFirst(final String indexName, final Object key) {
        return Optional.ofNullable(this.getFirst(indexName, key));
    }

    /**
     * Query indexes and look up all matching value.
     *
     * @param query query to execute
     * @param limit limit results to the first x number of items
     * @return values associated with this key or an empty list
     */
    List<V> get(final Query query, int limit);


    default List<V> get(final Query query) {
        return this.get(query, -1);
    }

    /**
     * Query indexes and look up the first matching value.
     *
     * @param query query to execute
     * @return first value matching the query
     */
    default V getFirst(final Query query) {
        final List<V> results = this.get(query, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Query indexes and look up the first matching value.
     *
     * @param query query to execute
     * @return first value matching the query
     */
    default Optional<V> findFirst(final Query query) {
        return Optional.ofNullable(this.getFirst(query));
    }

    /**
     * Find index with name. This is the same as {@link Store#findIndex(String)}, but returns a null instead of an optional if an index
     * cannot be found.
     *
     * @param indexName name of index to lookup
     * @return index
     */
    Index<V> getIndex(String indexName);

    /**
     * Get all indexes
     *
     * @return indexes
     */
    Collection<Index<V>> getIndexes();

    /**
     * Remove object by the query
     */
    default List<V> remove(final Query query) {
        return this.remove(query, -1);
    }

    /**
     * Remove object by the query with a limit
     */
    List<V> remove(final Query query, int limit);

    /**
     * Remove all indexes associated with this store
     */
    default void removeAllIndexes() {
        new ArrayList<>(this.getIndexes()).forEach(this::removeIndex);
    }

    /**
     * Find index with name. This is the same as {@link Store#getIndex(String)}, but returns an optional instead of a null if an index
     * cannot be found.
     *
     * @param indexName name of index to lookup
     * @return index optional
     */
    default Optional<Index<V>> findIndex(final String indexName) {
        return Optional.ofNullable(this.getIndex(indexName));
    }

    /**
     * Remove an index from store
     *
     * @param indexName index to remove
     * @return true if removed successfully
     */
    boolean removeIndex(String indexName);

    /**
     * Remove an index from store
     *
     * @param index index to remove
     * @return true if removed successfully
     */
    boolean removeIndex(Index<V> index);

    /**
     * Clear the existing indexes and reindex the entire store. This can be a slow operation depending on the number of items in the store
     * and total number of indexes.
     *
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    void reindex() throws IndexException;

    /**
     * Reindex a collection of items. This method will need to be called anytime a change is made to items stored within the store that
     * causes its indexes to become out of date.
     *
     * @param items items to reindex
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    void reindex(Collection<V> items) throws IndexException;

    /**
     * Reindex a particular item. This method will need to be called anytime a change is made to an item stored within the store that causes
     * its indexes to become out of date.
     *
     * @param item item to reindex
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    void reindex(final V item) throws IndexException;

    /**
     * Adds all item to the store and indexes them. If an item already exists in the store, it will be re-indexed.
     *
     * @param items items to on
     * @return true if one or more items did not previously exist in the store.
     * @throws IndexException thrown if one or more indexes failed with exceptions.
     */
    default boolean addAll(final V[] items) throws IndexException {
        return this.addAll(Arrays.asList(items));
    }

    /**
     * Create a copy of this store. This can be an expensive operation depending on the number of items and indexes present. The copied
     * store will be fully independent from this store. Any changes made to the copy will not reflect back onto this store.
     *
     * @return copy
     */
    Store<V> copy();

    /**
     * Returns an unmodifiable view of this store. This method allows modules to provide users with "read-only" access to internal
     * collections. Query operations on the returned store "read through" the backed store, and attempts to modify the returned store,
     * whether direct or via its iterator, result in an <tt>UnsupportedOperationException</tt>.
     *
     * <p>
     *
     * @return an unmodifiable view of this store.
     */
    default Store<V> unmodifiableStore() {
        return new UnmodifiableStore<>(this);
    }

    /**
     * Returns a synchronized (thread-safe) map Store backed by this store. In order to guarantee serial access, it is critical that
     * <strong>all</strong> access to the backing store is accomplished through the returned store.
     *
     * <p>Any references held to {@link Index}es should be discarded. Call {@link
     * Store#getIndex(String)} on the synchronized store to obtain synchronized indexes.
     *
     * <p>It is imperative that the user manually synchronize on the returned Store when iterating
     * over it:
     *
     * <pre>
     *  Store store = oldStore.synchronizedStore();
     *      ...
     *  synchronized (store) {
     *      Iterator i = store.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * <p>
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * @return synchronized store
     */
    default Store<V> synchronizedStore() {
        return new SynchronizedStore<>(this);
    }

    /**
     * Lock reindexing for to save performance when adding many entries in different methods
     *
     * @param lockIndexing true or false
     */
    void lockIndexing(boolean lockIndexing);

    /**
     * Create a query. Replacement for using {@link Store#get(String, Object, int)}
     */
    StoreQuery<V> createQuery();
}
