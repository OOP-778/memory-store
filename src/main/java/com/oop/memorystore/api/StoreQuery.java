package com.oop.memorystore.api;

import com.oop.memorystore.implementation.StoreQueryImpl;
import com.oop.memorystore.implementation.query.QueryOperator;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Usage at {@link Store#createQuery()}
 * @param <V> Store value type
 */
public interface StoreQuery<V> {
    /**
     * Filter by index name & key
     *
     * @param indexName name of created index
     * @param equals    key value
     * @return instance of this
     */
    StoreQueryImpl<V> filter(String indexName, Object equals);

    /**
     * Filter by index name & key
     *
     * @param indexName name of created index
     * @param operator  how this filter should be done
     * @param equals    key value
     * @return instance of this
     */
    StoreQueryImpl<V> filter(String indexName, QueryOperator operator, Object... equals);

    /**
     * Convert your query into stream with filtered values
     *
     * @return {@link Stream<V>}
     */
    Stream<V> asStream();

    /**
     * Collect to your collection results of the query
     *
     * @param collection Your collection
     * @param <T>        the type of collection
     * @return instance of your passed collection
     */
    <T extends Collection<V>> T collect(T collection);

    /**
     * Get first filtered value
     */
    Optional<V> first();
}
